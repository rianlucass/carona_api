package com.rianlucas.carona_api.infra.exceptions.auth;

import com.rianlucas.carona_api.infra.exceptions.BusinessException;

public class TokenGenerationException extends BusinessException {
    
    public TokenGenerationException(Throwable cause) {
        super("Erro ao gerar token de autenticação.", "AUTH_003", cause);
    }
}
