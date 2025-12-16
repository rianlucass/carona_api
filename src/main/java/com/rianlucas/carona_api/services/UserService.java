package com.rianlucas.carona_api.services;

import java.sql.Date;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rianlucas.carona_api.domain.user.AccountStatus;
import com.rianlucas.carona_api.domain.user.RegisterCompletedDTO;
import com.rianlucas.carona_api.domain.user.RegisterDTO;
import com.rianlucas.carona_api.domain.user.User;
import com.rianlucas.carona_api.domain.user.UserRole;
import com.rianlucas.carona_api.infra.exceptions.auth.EmailNotVerifiedException;
import com.rianlucas.carona_api.infra.exceptions.user.CpfAlreadyExistsException;
import com.rianlucas.carona_api.infra.exceptions.user.EmailAlreadyExistsException;
import com.rianlucas.carona_api.infra.exceptions.user.PhoneAlreadyExistsException;
import com.rianlucas.carona_api.infra.exceptions.user.UserNotFoundException;
import com.rianlucas.carona_api.infra.exceptions.user.UsernameAlreadyExistsException;
import com.rianlucas.carona_api.infra.exceptions.validation.InvalidDateFormatException;
import com.rianlucas.carona_api.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private FileStorageService fileStorageService;

    @Transactional
    public String registerUser(RegisterDTO registerDTO) {
        // valido se o email já existe
        if (userRepository.findByEmail(registerDTO.email()) != null) {
            throw new EmailAlreadyExistsException(registerDTO.email());
        }
        if (userRepository.findByUsername(registerDTO.username()) != null) {
            throw new UsernameAlreadyExistsException(registerDTO.username());
        }
        if (userRepository.findByPhone(registerDTO.phone()) != null) {
            throw new PhoneAlreadyExistsException(registerDTO.phone());
        }

        // crio o novo usuário com todos os dados
        User newUser = new User();
        newUser.setEmail(registerDTO.email());
        newUser.setPassword(passwordEncoder.encode(registerDTO.password()));
        newUser.setName(registerDTO.name());
        newUser.setUsername(registerDTO.username());
        newUser.setPhone(registerDTO.phone());
        
        // converto String para Date com validação
        if (registerDTO.birthDate() != null && !registerDTO.birthDate().isEmpty()) {
            try {
                newUser.setBirthDate(Date.valueOf(registerDTO.birthDate()));
            } catch (IllegalArgumentException e) {
                throw new InvalidDateFormatException();
            }
        }
        
        newUser.setGender(registerDTO.gender());

        // defino valores padrões se não fornecidos
        newUser.setDriver(false);
        newUser.setAccountStatus(AccountStatus.ACTIVE);
        newUser.setRole(UserRole.USER);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(newUser);

        // Envia código de verificação automaticamente
        try {
            emailVerificationService.requestVerificationCode(registerDTO.email());
        } catch (Exception e) {
            System.err.println("Erro ao enviar código de verificação: " + e.getMessage());
        }

        String token = tokenService.generateToken(newUser);
        return token;
    }

    //registrar o restante das variaveis do usuário depois que o email for verificado
    @Transactional
    public String completeUserProfile(String email, RegisterCompletedDTO completedDTO) {
        User user = (User) userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException(email);
        }

        if (user.getEmailVerified() == null || !user.getEmailVerified()) {
            throw new EmailNotVerifiedException(email);
        }
        
        // Verifica se o CPF já está em uso por outro usuário( trocar por uma validação real no futuro com api externa)
        if (completedDTO.cpf() != null && !completedDTO.cpf().isEmpty()) {
            User existingUserWithCpf = (User) userRepository.findByCpf(completedDTO.cpf());
            if (existingUserWithCpf != null && !existingUserWithCpf.getId().equals(user.getId())) {
                throw new CpfAlreadyExistsException(completedDTO.cpf());
            }
        }

        user.setPhotoUrl(fileStorageService.uploadPhoto(completedDTO.photo()));
        user.setCpf(completedDTO.cpf());
        user.setCity(completedDTO.city());
        user.setState(completedDTO.state());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return tokenService.generateToken(user);
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }

    public void markEmailAsVerified(String email) {
        User user = (User) userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException(email);
        }
        user.setEmailVerified(true);
        userRepository.save(user);
    }

    public String authenticateUser(String email, String password) {
        // Autentica o usuário
        var usernamePassword = new UsernamePasswordAuthenticationToken(email, password);
        Authentication auth = authenticationManager.authenticate(usernamePassword);
        
        User user = (User) auth.getPrincipal();
        
        // Verifica se o email foi verificado
        if (user.getEmailVerified() == null || !user.getEmailVerified()) {
            throw new EmailNotVerifiedException(email);
        }
        
        // Gera e retorna o token
        return tokenService.generateToken(user);
    }
}
