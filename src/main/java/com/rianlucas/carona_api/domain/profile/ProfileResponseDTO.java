package com.rianlucas.carona_api.domain.profile;

import java.sql.Date;

public record ProfileResponseDTO(
    String username,
    String name,
    String email,
    String phone,
    String photoUrl,
    boolean isDriver,
    String city,
    String state,
    String cpf,
    String gender,
    Date birthDate
) {}
