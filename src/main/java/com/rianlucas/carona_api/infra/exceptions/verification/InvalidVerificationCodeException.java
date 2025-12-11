package com.rianlucas.carona_api.infra.exceptions.verification;

import com.rianlucas.carona_api.infra.exceptions.BusinessException;

public class InvalidVerificationCodeException extends BusinessException {
    
    public InvalidVerificationCodeException() {
        super("Código de verificação inválido ou expirado.", "VERIFICATION_001");
    }
}
