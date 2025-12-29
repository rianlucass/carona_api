package com.rianlucas.carona_api.domain.profile;

import org.springframework.web.multipart.MultipartFile;

public record PhotoUpdateDTO(
    MultipartFile photo
) {}
