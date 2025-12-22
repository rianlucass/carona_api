package com.rianlucas.carona_api.domain.password;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record PasswordResetRequestDTO(
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email deve ser válido")
    String email
) {}
