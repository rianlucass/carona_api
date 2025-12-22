package com.rianlucas.carona_api.infra.exceptions.auth;

import com.rianlucas.carona_api.infra.exceptions.BusinessException;

public class InvalidTokenException extends BusinessException {
    
    public InvalidTokenException() {
        super("Token inválido ou já utilizado", "INVALID_TOKEN");
    }
    
    public InvalidTokenException(String message) {
        super(message, "INVALID_TOKEN");
    }
}
