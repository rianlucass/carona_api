package com.rianlucas.carona_api.domain.profile;

import java.sql.Date;

import org.springframework.web.multipart.MultipartFile;

public record ProfileUpdateDTO(
    String username,
    String name,
    String phone,
    MultipartFile photo,
    String city,
    String state,
    String cpf,
    String gender,
    Date birthDate
) {}
