package com.rianlucas.carona_api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.rianlucas.carona_api.domain.email.EmailCodeRequest;
import com.rianlucas.carona_api.domain.email.EmailVerificationRequest;
import com.rianlucas.carona_api.domain.email.EmailVerificationResponseDTO;
import com.rianlucas.carona_api.domain.response.ApiResponse;
import com.rianlucas.carona_api.services.EmailVerificationService;
import com.rianlucas.carona_api.services.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/email-verification")
public class EmailVerificationController {

    @Autowired
    private EmailVerificationService verificationService;
    
    @Autowired
    private UserService userService;
    
    // 1. Solicitar código de verificação
    @PostMapping("/request-code")
    public ResponseEntity<?> requestVerificationCode(@Valid @RequestBody EmailCodeRequest request) {
        verificationService.requestVerificationCode(request.getEmail());
        return ResponseEntity.ok(new ApiResponse<>(true, "Código enviado para o email"));
    }
    
    // 2. Verificar código
    @PostMapping("/verify")
    public ResponseEntity<?> verifyEmail(@Valid @RequestBody EmailVerificationRequest request) {
        verificationService.verifyCode(request.getEmail(), request.getCode());
        
        // Marca o email como verificado no banco de dados
        userService.markEmailAsVerified(request.getEmail());
        
        EmailVerificationResponseDTO response = new EmailVerificationResponseDTO(
            true,
            "Email verificado com sucesso! Você já pode fazer login.",
            request.getEmail()
        );
        
        return ResponseEntity.ok(new ApiResponse<>(true, "Email verificado", response));
    }
    
    // 3. Reenviar código (opcional)
    @PostMapping("/resend-code")
    public ResponseEntity<?> resendVerificationCode(@Valid @RequestBody EmailCodeRequest request) {
        // Verifica se o usuário existe
        if (!userService.emailExists(request.getEmail())) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(false, "Email não encontrado"));
        }
        
        verificationService.requestVerificationCode(request.getEmail());
        return ResponseEntity.ok(new ApiResponse<>(true, "Novo código enviado para o email"));
    }
}
