package com.rianlucas.carona_api.domain.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record PhoneUpdateDTO(
    @NotBlank(message = "O telefone não pode estar vazio")
    @Pattern(regexp = "^\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}$", 
             message = "Formato de telefone inválido. Use: (XX) 9XXXX-XXXX ou variações")
    String phone
) {
}
