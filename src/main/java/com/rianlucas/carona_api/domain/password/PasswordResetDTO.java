package com.rianlucas.carona_api.domain.password;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PasswordResetDTO(
    @NotBlank(message = "Token é obrigatório")
    String token,
    
    @NotBlank(message = "Nova senha é obrigatória")
    @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
        message = "A senha deve conter pelo menos: 1 letra maiúscula, 1 letra minúscula, 1 número e 1 caractere especial (@#$%^&+=)"
    )
    String newPassword,
    
    @NotBlank(message = "Confirmação de senha é obrigatória")
    String confirmPassword
) {}
