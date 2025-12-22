package com.rianlucas.carona_api.infra.exceptions.validation;

import com.rianlucas.carona_api.infra.exceptions.BusinessException;

public class InvalidPasswordException extends BusinessException {
    
    public InvalidPasswordException(String message) {
        super(message, "INVALID_PASSWORD");
    }
    
}
