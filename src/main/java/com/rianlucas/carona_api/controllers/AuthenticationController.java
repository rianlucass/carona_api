package com.rianlucas.carona_api.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.rianlucas.carona_api.domain.response.ApiResponse;
import com.rianlucas.carona_api.domain.user.AuthenticationDTO;
import com.rianlucas.carona_api.domain.user.LoginResponseDTO;
import com.rianlucas.carona_api.domain.user.RegisterCompletedDTO;
import com.rianlucas.carona_api.domain.user.RegisterDTO;
import com.rianlucas.carona_api.domain.user.RegisterResponseDTO;
import com.rianlucas.carona_api.services.UserService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated AuthenticationDTO authDTO) {
        String token = userService.authenticateUser(authDTO.email(), authDTO.password());
        return ResponseEntity.ok(new ApiResponse<>(true, "Login realizado com sucesso", new LoginResponseDTO(token)));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Validated RegisterDTO registerDTO) {
        String token = userService.registerUser(registerDTO);
        
        RegisterResponseDTO response = new RegisterResponseDTO(
            "Usuário registrado com sucesso. Código de verificação enviado para o email.",
            registerDTO.email(),
            true // emailVerificationRequired
        );
        
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(new ApiResponse<>(true, "Cadastro realizado com sucesso", new LoginResponseDTO(token)));
    }

    @PostMapping(value = "/registerComplete/{email}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> completeRegistration( @PathVariable String email, @ModelAttribute @Validated RegisterCompletedDTO registerDTO) {
        String token = userService.completeUserProfile(email, registerDTO);
        
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(new ApiResponse<>(true, "Cadastro completo com sucesso", new LoginResponseDTO(token)));
    }

    
    
    
}
