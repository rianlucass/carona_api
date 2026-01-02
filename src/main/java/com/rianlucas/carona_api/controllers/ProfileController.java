package com.rianlucas.carona_api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rianlucas.carona_api.domain.profile.PhoneResponseDTO;
import com.rianlucas.carona_api.domain.profile.PhoneUpdateDTO;
import com.rianlucas.carona_api.domain.profile.PhotoResponseDTO;
import com.rianlucas.carona_api.domain.profile.ProfileResponseDTO;
import com.rianlucas.carona_api.domain.profile.UsernameResponseDTO;
import com.rianlucas.carona_api.domain.profile.UsernameUpdateDTO;

import jakarta.validation.Valid;
import com.rianlucas.carona_api.domain.response.ApiResponse;
import com.rianlucas.carona_api.domain.user.User;
import com.rianlucas.carona_api.services.ProfileService;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileService profileService;

    @GetMapping
    public ResponseEntity<ApiResponse<ProfileResponseDTO>> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        
        User user = (User) userDetails;
        ProfileResponseDTO profile = profileService.getProfile(user.getId());
        
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Perfil recuperado com sucesso", profile)
        );
    }

    @PutMapping(value = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PhotoResponseDTO>> updatePhoto(
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute com.rianlucas.carona_api.domain.profile.PhotoUpdateDTO photoData) {
        User user = (User) userDetails;
        PhotoResponseDTO updatedPhoto = profileService.updatePhoto(user.getId(), photoData);
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Foto de perfil atualizada com sucesso: ", updatedPhoto)
        );
    }

    @PutMapping("/username")
    public ResponseEntity<ApiResponse<UsernameResponseDTO>> editUsername(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody UsernameUpdateDTO usernameData) {
        User user = (User) userDetails;
        UsernameResponseDTO updatedUsername = profileService.editUsername(user.getId(), usernameData.newUsername());
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Nome de usuário atualizado com sucesso", updatedUsername)
        );
    }

    @PutMapping("/phone")
    public ResponseEntity<ApiResponse<PhoneResponseDTO>> editPhone(
        @AuthenticationPrincipal UserDetails userDetails,
        @Valid @RequestBody PhoneUpdateDTO phoneData) {
            
        User user = (User) userDetails;

        PhoneResponseDTO updatedPhone = profileService.editPhone(user.getId(), phoneData.phone());
        return ResponseEntity.ok(
            new ApiResponse<>(true, "Telefone atualizado com sucesso", updatedPhone)
        );
    }
    


}
