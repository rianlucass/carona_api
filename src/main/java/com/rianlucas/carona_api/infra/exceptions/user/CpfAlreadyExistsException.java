package com.rianlucas.carona_api.infra.exceptions.user;

import com.rianlucas.carona_api.infra.exceptions.BusinessException;

public class CpfAlreadyExistsException extends BusinessException {
    
    public CpfAlreadyExistsException(String cpf) {
        super("O CPF " + cpf + " já está em uso.", "USER_004");
    }
}
