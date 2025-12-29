package com.rianlucas.carona_api.domain.profile;

import jakarta.validation.constraints.Size;

public record UsernameResponseDTO(

    @Size(min = 5, max = 20, message = "Username must be between 5 and 20 characters long")
    String username
) {
    
}
