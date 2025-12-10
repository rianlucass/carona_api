package com.rianlucas.carona_api.services;

import java.sql.Date;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.rianlucas.carona_api.domain.user.AccountStatus;
import com.rianlucas.carona_api.domain.user.RegisterDTO;
import com.rianlucas.carona_api.domain.user.User;
import com.rianlucas.carona_api.domain.user.UserRole;
import com.rianlucas.carona_api.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailVerificationService emailVerificationService;

    public String registerUser(RegisterDTO registerDTO) {
        // valido se o email já existe
        if (userRepository.findByEmail(registerDTO.email()) != null) {
            throw new RuntimeException("Email esta ja em uso.");
        }
        if (userRepository.findByUsername(registerDTO.username()) != null) {
            throw new RuntimeException("Username ja cadastrado.");
        }
        if (userRepository.findByPhone(registerDTO.phone()) != null) {
            throw new RuntimeException("Phone invalido ou ja cadastrado.");
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
                throw new RuntimeException("Invalid date format. Use YYYY-MM-DD.");
            }
        }
        
        newUser.setGender(registerDTO.gender());
        newUser.setDriver(registerDTO.isDriver());
        
        // defino valores padrões se não fornecidos
        newUser.setAccountStatus(AccountStatus.ACTIVE);
        newUser.setRole(UserRole.USER);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());
        userRepository.save(newUser);

        // Envia código de verificação automaticamente
        try {
            emailVerificationService.requestVerificationCode(registerDTO.email());
        } catch (Exception e) {
            // Log do erro, mas não falha o registro
            System.err.println("Erro ao enviar código de verificação: " + e.getMessage());
        }

        return "Usuário registrado com sucesso. Código de verificação enviado para o email.";
    }

    public boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }

    public void markEmailAsVerified(String email) {
        User user = (User) userRepository.findByEmail(email);
        if (user == null) {
            throw new RuntimeException("Usuário não encontrado.");
        }
        user.setEmailVerified(true);
        userRepository.save(user);
    }
}
