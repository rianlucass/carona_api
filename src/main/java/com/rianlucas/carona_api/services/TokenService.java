package com.rianlucas.carona_api.services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.rianlucas.carona_api.domain.user.User;
import com.rianlucas.carona_api.infra.exceptions.auth.TokenGenerationException;
import com.rianlucas.carona_api.infra.exceptions.auth.TokenValidationException;

@Service
public class TokenService {
    
    @Value("${api.security.token.secret}")
    private String secret;

    public String generateToken(User user) {
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(user.getUsername()) // Email do usuário
                    .withClaim("userId", user.getId()) // ID único do usuário
                    .withClaim("name", user.getName()) // Nome do usuário
                    .withClaim("role", user.getRole().toString()) // Papel (USER, ADMIN)
                    .withClaim("emailVerified", user.getEmailVerified() != null ? user.getEmailVerified() : false) // Email verificado?
                    .withClaim("profileComplete", user.getCpf() != null && user.getCity() != null) // Perfil completo?
                    .withExpiresAt(genExpirantionDate())
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception) {
            throw new TokenGenerationException(exception);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTCreationException exception){
            throw new TokenValidationException(exception);
        }
    }

    private Instant genExpirantionDate() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
    
}
