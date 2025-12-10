package com.rianlucas.carona_api.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterResponseDTO {
    private String message;
    private String email;
    private boolean emailVerificationRequired;
}
