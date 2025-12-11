package com.rianlucas.carona_api.infra.exceptions.auth;

import com.rianlucas.carona_api.infra.exceptions.BusinessException;

public class InvalidCredentialsException extends BusinessException {
    
    public InvalidCredentialsException() {
        super("Email ou senha inválidos.", "AUTH_002");
    }
}
