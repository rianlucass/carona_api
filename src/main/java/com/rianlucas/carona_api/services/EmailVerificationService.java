package com.rianlucas.carona_api.services;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rianlucas.carona_api.infra.exceptions.verification.InvalidVerificationCodeException;
import com.rianlucas.carona_api.infra.exceptions.verification.VerificationCodeExpiredException;

@Service
public class EmailVerificationService {
    // Armazena códigos temporários: email -> {código, expiração}
    private final Map<String, VerificationData> verificationStorage = new ConcurrentHashMap<>();
    
    @Autowired
    private EmailService emailService;
    
    // Solicitar novo código
    public void requestVerificationCode(String email) {
        // 1. Gerar código de 6 dígitos
        String code = generateSixDigitCode();
        
        // 2. Definir expiração (10 minutos)
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(10);
        
        // 3. Armazenar
        verificationStorage.put(email, new VerificationData(code, expirationTime));
        
        // 4. Enviar email
        emailService.sendVerificationCode(email, code);
    }
    
    // Verificar código
    public void verifyCode(String email, String code) {
        VerificationData data = verificationStorage.get(email);
        
        if (data == null) {
            throw new InvalidVerificationCodeException();
        }
        
        // Verificar se expirou
        if (LocalDateTime.now().isAfter(data.getExpirationTime())) {
            verificationStorage.remove(email);
            throw new VerificationCodeExpiredException();
        }
        
        // Verificar se o código está correto
        if (!data.getCode().equals(code)) {
            throw new InvalidVerificationCodeException();
        }
        
        // Remove o código após verificação bem-sucedida
        verificationStorage.remove(email);
    }
    
    // Gerar código de 6 dígitos
    private String generateSixDigitCode() {
        Random random = new Random();
        int number = random.nextInt(999999);
        return String.format("%06d", number); // Garante 6 dígitos com zeros à esquerda
    }
    
    // Classe interna para armazenar dados de verificação
    private static class VerificationData {
        private final String code;
        private final LocalDateTime expirationTime;
        
        public VerificationData(String code, LocalDateTime expirationTime) {
            this.code = code;
            this.expirationTime = expirationTime;
        }
        
        public String getCode() { return code; }
        public LocalDateTime getExpirationTime() { return expirationTime; }
    }
}
