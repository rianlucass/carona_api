package com.rianlucas.carona_api.infra.exceptions.validation;

import com.rianlucas.carona_api.infra.exceptions.BusinessException;

public class InvalidDateFormatException extends BusinessException {
    
    public InvalidDateFormatException() {
        super("Formato de data inválido. Use o formato YYYY-MM-DD.", "VALIDATION_001");
    }
}
