package com.rianlucas.carona_api.infra.exceptions.file;

import com.rianlucas.carona_api.infra.exceptions.BusinessException;

/**
 * Exception lançada quando o tipo de arquivo não é permitido
 */
public class InvalidFileTypeException extends BusinessException {
    
    public InvalidFileTypeException(String message) {
        super(message, "INVALID_FILE_TYPE");
    }
}
