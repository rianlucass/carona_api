package com.rianlucas.carona_api.services;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.digest.DigestUtils;

import com.rianlucas.carona_api.domain.password.PasswordResetToken;
import com.rianlucas.carona_api.domain.user.User;
import com.rianlucas.carona_api.infra.exceptions.auth.InvalidTokenException;
import com.rianlucas.carona_api.infra.exceptions.auth.TokenExpiredException;
import com.rianlucas.carona_api.infra.exceptions.user.UserNotFoundException;
import com.rianlucas.carona_api.infra.exceptions.validation.InvalidPasswordException;
import com.rianlucas.carona_api.repositories.PasswordResetTokenRepository;
import com.rianlucas.carona_api.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class PasswordResetService {
    
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;
    
    /**
     * Solicita um reset de senha enviando um token por email
     * @param email Email do usuário
     * @return Email para confirmação
     */
    @Transactional
    public String requestPasswordReset(String email) {
        User user = (User) userRepository.findByEmail(email);

        if (user == null || user.getEmail() == null) {
            throw new UserNotFoundException(email);
        }

        String token = UUID.randomUUID().toString();
        String tokenHash = DigestUtils.sha256Hex(token);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user);
        resetToken.setTokenHash(tokenHash);
        resetToken.setExpiresAt(LocalDateTime.now().plusMinutes(15));

        passwordResetTokenRepository.save(resetToken);
        
        // Enviar email com token
        emailService.sendPasswordResetEmail(user.getEmail(), token);

        return user.getEmail();
    }
    
    /**
     * Valida o token de reset de senha
     * @param token Token a ser validado
     * @return PasswordResetToken se válido
     * @throws InvalidTokenException se token não existe ou já foi usado
     * @throws TokenExpiredException se token expirou
     */
    public PasswordResetToken validateToken(String token) {
        String tokenHash = DigestUtils.sha256Hex(token);
        
        Optional<PasswordResetToken> resetTokenOpt = 
            passwordResetTokenRepository.findByTokenHashAndUsedAtIsNull(tokenHash);
        
        if (resetTokenOpt.isEmpty()) {
            throw new InvalidTokenException();
        }
        
        PasswordResetToken resetToken = resetTokenOpt.get();
        
        // Verificar se token expirou
        if (resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenExpiredException();
        }
        
        return resetToken;
    }
    
    /**
     * Realiza o reset da senha
     * @param token Token de reset
     * @param newPassword Nova senha
     * @param confirmPassword Confirmação da senha
     * @return Email do usuário
     */
    @Transactional
    public String resetPassword(String token, String newPassword, String confirmPassword) {
        // Validar se as senhas coincidem
        if (!newPassword.equals(confirmPassword)) {
            throw new InvalidPasswordException("As senhas não coincidem");
        }

        PasswordResetToken resetToken = validateToken(token);
        User user = resetToken.getUser();
        
        String encryptedPassword = new BCryptPasswordEncoder().encode(newPassword);
        user.setPassword(encryptedPassword);
        userRepository.save(user);
        
        resetToken.setUsedAt(LocalDateTime.now());
        passwordResetTokenRepository.save(resetToken);
        
        return user.getEmail();
    }

}
