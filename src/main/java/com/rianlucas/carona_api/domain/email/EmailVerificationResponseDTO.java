package com.rianlucas.carona_api.domain.email;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmailVerificationResponseDTO {
    private boolean success;
    private String message;
    private String email;
}
