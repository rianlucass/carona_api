package com.rianlucas.carona_api.infra.exceptions.user;

import com.rianlucas.carona_api.infra.exceptions.BusinessException;

public class PhoneAlreadyExistsException extends BusinessException {
    
    public PhoneAlreadyExistsException(String phone) {
        super("O telefone " + phone + " já está em uso.", "USER_003");
    }
}
