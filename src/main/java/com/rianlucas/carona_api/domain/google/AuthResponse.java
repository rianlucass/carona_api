package com.rianlucas.carona_api.domain.google;

public record AuthResponse(
    String token,
    String email,
    String name,
    String pictureUrl,
    boolean profileComplete
) {
    
}
