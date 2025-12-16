package com.rianlucas.carona_api.domain.user;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterCompletedDTO(
    MultipartFile photo, // Foto é opcional
    
    @NotBlank(message = "CPF is mandatory")
    @Pattern(regexp = "^\\d{11}$", message = "CPF must be 11 digits")
    String cpf,
    
    @NotBlank(message = "State is mandatory")
    @Size(min = 2, max = 2, message = "State must be 2 characters (e.g., SP, RJ)")
    @Pattern(regexp = "^[A-Z]{2}$", message = "State must be 2 uppercase letters")
    String state,
    
    @NotBlank(message = "City is mandatory")
    @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
    String city
) {
}
