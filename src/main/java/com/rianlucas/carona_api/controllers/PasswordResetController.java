package com.rianlucas.carona_api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rianlucas.carona_api.domain.password.PasswordResetDTO;
import com.rianlucas.carona_api.domain.password.PasswordResetRequestDTO;
import com.rianlucas.carona_api.domain.password.PasswordResetResponseDTO;
import com.rianlucas.carona_api.domain.response.ApiResponse;
import com.rianlucas.carona_api.services.PasswordResetService;

@RestController
@RequestMapping("/auth/password")
public class PasswordResetController {
    
    @Autowired
    private PasswordResetService passwordResetService;
    

    //para solicitar reset de senha
    @PostMapping("/request-reset")
    public ResponseEntity<?> requestPasswordReset(@RequestBody @Validated PasswordResetRequestDTO request) {
        String email = passwordResetService.requestPasswordReset(request.email());
        
        PasswordResetResponseDTO response = new PasswordResetResponseDTO(
            "Um email com instruções para redefinir sua senha foi enviado",
            email
        );
        
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(new ApiResponse<>(true, "Email de redefinição enviado com sucesso", response));
    }
    
    //para resetar a senha
    @PostMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody @Validated PasswordResetDTO request) {
        String email = passwordResetService.resetPassword(
            request.token(), 
            request.newPassword(), 
            request.confirmPassword()
        );
        
        PasswordResetResponseDTO response = new PasswordResetResponseDTO(
            "Senha redefinida com sucesso",
            email
        );
        
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(new ApiResponse<>(true, "Senha alterada com sucesso", response));
    }
}
