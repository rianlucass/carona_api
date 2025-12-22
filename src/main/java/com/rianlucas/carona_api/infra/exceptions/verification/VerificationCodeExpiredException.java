package com.rianlucas.carona_api.infra.exceptions.verification;

import com.rianlucas.carona_api.infra.exceptions.BusinessException;

public class VerificationCodeExpiredException extends BusinessException {
    
    public VerificationCodeExpiredException() {
        super("Código de verificação expirado. Solicite um novo código.", "VERIFICATION_002");
    }
}
