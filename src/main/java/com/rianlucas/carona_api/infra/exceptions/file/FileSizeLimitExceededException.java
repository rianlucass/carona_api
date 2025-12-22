package com.rianlucas.carona_api.infra.exceptions.file;

import com.rianlucas.carona_api.infra.exceptions.BusinessException;

/**
 * Exception lançada quando o tamanho do arquivo excede o limite permitido
 */
public class FileSizeLimitExceededException extends BusinessException {
    
    public FileSizeLimitExceededException(String message) {
        super(message, "FILE_SIZE_LIMIT_EXCEEDED");
    }
}
