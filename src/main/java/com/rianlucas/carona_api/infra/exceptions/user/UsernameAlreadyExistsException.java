package com.rianlucas.carona_api.infra.exceptions.user;

import com.rianlucas.carona_api.infra.exceptions.BusinessException;

public class UsernameAlreadyExistsException extends BusinessException {
    
    public UsernameAlreadyExistsException(String username) {
        super("O username " + username + " já está em uso.", "USER_002");
    }
}
