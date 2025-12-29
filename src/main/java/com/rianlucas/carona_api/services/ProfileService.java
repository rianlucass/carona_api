package com.rianlucas.carona_api.services;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rianlucas.carona_api.domain.profile.PhotoResponseDTO;
import com.rianlucas.carona_api.domain.profile.PhotoUpdateDTO;
import com.rianlucas.carona_api.domain.profile.ProfileResponseDTO;
import com.rianlucas.carona_api.domain.profile.UsernameResponseDTO;
import com.rianlucas.carona_api.domain.user.User;
import com.rianlucas.carona_api.infra.config.FileUrlBuilder;
import com.rianlucas.carona_api.infra.exceptions.user.UserNotFoundException;
import com.rianlucas.carona_api.infra.exceptions.user.UsernameEditException;
import com.rianlucas.carona_api.repositories.UserRepository;

@Service
public class ProfileService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private FileUrlBuilder fileUrlBuilder;

    public ProfileResponseDTO getProfile(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Construir URL completa da foto
        String photoUrl = fileUrlBuilder.buildFileUrl(user.getPhotoUrl());

        return new ProfileResponseDTO(
                user.getUsernameField(),
                user.getName(),
                user.getEmail(),
                user.getPhone(),
                photoUrl,
                user.isDriver(),
                user.getCity(),
                user.getState(),
                user.getCpf(),
                user.getGender(),
                user.getBirthDate());
    }

    @Transactional
    public PhotoResponseDTO updatePhoto(String userId, PhotoUpdateDTO photoData) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        if (photoData.photo() != null && !photoData.photo().isEmpty()) {
            String photoPath = fileStorageService.uploadPhoto(
                    photoData.photo(),
                    com.rianlucas.carona_api.domain.file.FileType.USER_PROFILE,
                    userId);
            if (photoPath != null) {
                user.setPhotoUrl(photoPath);
            }
        }

        user.setUpdatedAt(java.time.LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        String photoUrl = fileUrlBuilder.buildFileUrl(updatedUser.getPhotoUrl());
        return new PhotoResponseDTO(
                photoUrl);
    }

    // função de editar username com regras: editado um vez, será bloqueado por 60
    // dias para nova edição
    private static final Duration USERNAME_EDIT_INTERVAL = Duration.ofDays(60);

    public UsernameResponseDTO editUsername(String userId, String newUsername) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Verifica se o novo username já está em uso por outro usuário
        if (userRepository.existsByUsername(newUsername) && !user.getUsernameField().equals(newUsername)) {
            throw new UsernameEditException("O username informado já está em uso. Escolha outro.");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime lastEdit = user.getUsernameLastEditedAt();

        if (lastEdit != null) {
            Duration elapsed = Duration.between(lastEdit, now);
            if (elapsed.compareTo(USERNAME_EDIT_INTERVAL) < 0) {
                long diasRestantes = USERNAME_EDIT_INTERVAL.minus(elapsed).toDays();
                if (diasRestantes > 1) {

                    throw new UsernameEditException("O username só pode ser alterado uma vez a cada 60 dias, sua próxima alteração estará disponível em " + (diasRestantes + 1) + " dias.");
                } else if (diasRestantes == 1) {
                    throw new UsernameEditException("O username só pode ser alterado uma vez a cada 60 dias, sua próxima alteração estará disponível em 1 dia.");
                } else {
                    throw new UsernameEditException("O username só pode ser alterado uma vez a cada 60 dias, sua próxima alteração estará disponível em menos de 1 dia.");
                }
            }
        }

        user.setUsername(newUsername);
        user.setUsernameLastEditedAt(now);
        user.setUpdatedAt(now);

        userRepository.save(user);

        return new UsernameResponseDTO(newUsername);
    }
}