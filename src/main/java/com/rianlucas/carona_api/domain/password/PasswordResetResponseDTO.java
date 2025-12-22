package com.rianlucas.carona_api.domain.password;

public record PasswordResetResponseDTO(
    String message,
    String email
) {}
