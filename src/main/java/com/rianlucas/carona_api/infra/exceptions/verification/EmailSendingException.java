package com.rianlucas.carona_api.infra.exceptions.verification;

import com.rianlucas.carona_api.infra.exceptions.BusinessException;

public class EmailSendingException extends BusinessException {
    
    public EmailSendingException(Throwable cause) {
        super("Erro ao enviar email de verificação. Tente novamente mais tarde.", "VERIFICATION_003", cause);
    }
}
