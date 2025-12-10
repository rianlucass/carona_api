package com.rianlucas.carona_api.domain.user;

public record AuthenticationDTO(
    String email,
    String password
) {

}
