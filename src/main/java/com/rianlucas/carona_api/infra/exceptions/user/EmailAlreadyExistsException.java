package com.rianlucas.carona_api.infra.exceptions.user;

import com.rianlucas.carona_api.infra.exceptions.BusinessException;

public class EmailAlreadyExistsException extends BusinessException {
    
    public EmailAlreadyExistsException(String email) {
        super("O email " + email + " já está em uso.", "USER_001");
    }
}
