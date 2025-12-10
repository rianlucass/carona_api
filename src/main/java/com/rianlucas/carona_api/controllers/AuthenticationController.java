package com.rianlucas.carona_api.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.rianlucas.carona_api.domain.response.ApiResponse;
import com.rianlucas.carona_api.domain.user.AuthenticationDTO;
import com.rianlucas.carona_api.domain.user.LoginResponseDTO;
import com.rianlucas.carona_api.domain.user.RegisterDTO;
import com.rianlucas.carona_api.domain.user.RegisterResponseDTO;
import com.rianlucas.carona_api.domain.user.User;
import com.rianlucas.carona_api.services.TokenService;
import com.rianlucas.carona_api.services.UserService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenService tokenService;
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Validated AuthenticationDTO authDTO) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(authDTO.email(), authDTO.password());
            var auth = this.authenticationManager.authenticate(usernamePassword);
            
            User user = (User) auth.getPrincipal();
            
            // Verifica se o email foi verificado
            if (!user.isEmailVerified()) {
                return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, "Email não verificado. Verifique seu email antes de fazer login."));
            }

            var token = tokenService.generateToken(user);

            return ResponseEntity.ok(new ApiResponse<>(true, "Login realizado com sucesso", new LoginResponseDTO(token)));
        } catch (Exception e) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(false, "Email ou senha inválidos"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Validated RegisterDTO registerDTO) {
        try {
            String message = userService.registerUser(registerDTO);
            
            RegisterResponseDTO response = new RegisterResponseDTO(
                message,
                registerDTO.email(),
                true // emailVerificationRequired
            );
            
            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, "Cadastro realizado com sucesso", response));
        } catch (RuntimeException e) {
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(false, e.getMessage()));
        }
    }
    
    
}
