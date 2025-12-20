package com.rianlucas.carona_api.infra.exceptions.user;

import com.rianlucas.carona_api.infra.exceptions.BusinessException;

public class ProfileIncompleteException extends BusinessException {
    
    public ProfileIncompleteException() {
        super("Perfil incompleto. Por favor, complete seu cadastro antes de continuar.", "USER_005");
    }
    
    public ProfileIncompleteException(String email) {
        super("O perfil do usuário " + email + " está incompleto. Complete o cadastro para acessar o sistema.", "USER_005");
    }
}
