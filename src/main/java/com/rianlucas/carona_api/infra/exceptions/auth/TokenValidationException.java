package com.rianlucas.carona_api.infra.exceptions.auth;

import com.rianlucas.carona_api.infra.exceptions.BusinessException;

public class TokenValidationException extends BusinessException {
    
    public TokenValidationException(Throwable cause) {
        super("Token inválido ou expirado.", "AUTH_004", cause);
    }
}
