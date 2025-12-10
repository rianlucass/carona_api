package com.rianlucas.carona_api.domain.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterDTO(

        @NotBlank(message = "Email is mandatory")
        @Email(message = "Invalid email format")
        String email,
        
        @NotBlank(message = "Password is mandatory")
        @Size(min = 8, max = 255, message = "Password must be at least 8 characters long")
        String password,

        @NotBlank(message = "Name is mandatory")
        @Size(min = 8, max = 255, message = "Name must be at least 8 characters long")
        String name,

        @NotBlank(message = "Username is mandatory")
        @Size(min = 4, max = 50, message = "Username must be between 4 and 50 characters long")
        String username,

        @NotBlank(message = "Phone is mandatory")
        @Pattern(regexp = "^\\d{10,11}$", message = "Phone must be 10 or 11 digits")
        String phone,

        @NotBlank(message = "Birth date is mandatory")
        @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Birth date must be in format YYYY-MM-DD")
        String birthDate,

        @NotBlank(message = "Gender is mandatory")
        @Pattern(regexp = "^[MFO]$", message = "Gender must be M (Male), F (Female), or O (Other)")
        String gender,
        boolean isDriver,
        AccountStatus accountStatus
) {
    
}
