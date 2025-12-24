package com.rianlucas.carona_api.services;

import java.util.Collections;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.rianlucas.carona_api.domain.google.AuthProvider;
import com.rianlucas.carona_api.domain.google.AuthResponse;
import com.rianlucas.carona_api.domain.user.AccountStatus;
import com.rianlucas.carona_api.domain.user.User;
import com.rianlucas.carona_api.domain.user.UserRole;
import com.rianlucas.carona_api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GoogleTokenVerifierService {
    
    @Value("${google.client.id}")
    private String googleClientId;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenService tokenService;

    public GoogleIdToken.Payload verifyToken(String idTokenString) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), 
                new GsonFactory()
            )
            .setAudience(Collections.singletonList(googleClientId))
            .build();

            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                return idToken.getPayload();
            } else {
                throw new IllegalArgumentException("Token ID inválido.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Falha ao verificar token: " + e.getMessage(), e);
        }
    }

    public AuthResponse buildAuthResponse(GoogleIdToken.Payload payload) {
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String pictureUrl = (String) payload.get("picture");
        
        User user = (User) userRepository.findByEmail(email);

        if(user != null) {
            // usuário já existe, gerar o token
            String token = tokenService.generateToken(user);
            boolean profileComplete = isProfileComplete(user);
            return new AuthResponse(token, email, name, pictureUrl, profileComplete);
        } else {
            // usuário não existe, criar novo
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(name);
            newUser.setUsername(generateUniqueUsername(email));
            newUser.setPhotoUrl(pictureUrl);
            newUser.setAuthProvider(AuthProvider.GOOGLE);
            newUser.setAccountStatus(AccountStatus.ACTIVE);
            newUser.setEmailVerified(true); 
            newUser.setRole(UserRole.USER); // Definir role padrão

            userRepository.save(newUser);

            String token = tokenService.generateToken(newUser);
            // Novo usuário sempre tem perfil incompleto (falta phone, cpf, birthDate)
            return new AuthResponse(token, email, name, pictureUrl, false);
        }
    }

    /**
     * Verifica se o perfil do usuário está completo
     * @param user Usuário a ser verificado
     * @return true se phone, cpf e birthDate estão preenchidos
     */
    private boolean isProfileComplete(User user) {
        return user.getPhone() != null && !user.getPhone().isEmpty() &&
               user.getCpf() != null && !user.getCpf().isEmpty() &&
               user.getBirthDate() != null;
    }

    /**
     * Gera um username único baseado no email
     * Se o username já existir, adiciona um número sequencial
     * @param email Email do usuário
     * @return Username único
     */
    private String generateUniqueUsername(String email) {
        String baseUsername = email.split("@")[0];
        String username = baseUsername;
        int counter = 1;

        // Enquanto o username existir, adiciona um número
        while (userRepository.findByUsername(username) != null) {
            username = baseUsername + counter;
            counter++;
        }

        return username;
    }

}
