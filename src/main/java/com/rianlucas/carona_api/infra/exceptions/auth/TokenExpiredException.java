package com.rianlucas.carona_api.infra.exceptions.auth;

import com.rianlucas.carona_api.infra.exceptions.BusinessException;

public class TokenExpiredException extends BusinessException {
    
    public TokenExpiredException() {
        super("Token expirado. Solicite um novo token de redefinição de senha", "TOKEN_EXPIRED");
    }
    
    public TokenExpiredException(String message) {
        super(message, "TOKEN_EXPIRED");
    }
}
