package com.rianlucas.carona_api.infra.exceptions.user;

import com.rianlucas.carona_api.infra.exceptions.BusinessException;

public class UsernameEditException extends BusinessException {

    public UsernameEditException(String message) {
        
        super( message, "USER_003");
    }
    
}
