package com.rianlucas.carona_api.infra.exceptions.auth;

import com.rianlucas.carona_api.infra.exceptions.BusinessException;

public class EmailNotVerifiedException extends BusinessException {
    
    public EmailNotVerifiedException() {
        super("Email não verificado. Por favor, verifique seu email antes de fazer login.", "AUTH_001");
    }
    
    public EmailNotVerifiedException(String email) {
        super("O email " + email + " não foi verificado. Por favor, verifique seu email antes de fazer login.", "AUTH_001");
    }
}
