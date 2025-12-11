package com.rianlucas.carona_api.infra.exceptions.user;

import com.rianlucas.carona_api.infra.exceptions.BusinessException;

public class UserNotFoundException extends BusinessException {
    
    public UserNotFoundException() {
        super("Usuário não encontrado.", "USER_004");
    }
    
    public UserNotFoundException(String identifier) {
        super("Usuário com identificador " + identifier + " não encontrado.", "USER_004");
    }
}
