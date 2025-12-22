package com.rianlucas.carona_api.infra.exceptions.file;

import com.rianlucas.carona_api.infra.exceptions.BusinessException;

/**
 * Exception lançada quando ocorre um erro durante o upload de arquivo
 */
public class FileUploadException extends BusinessException {
    
    public FileUploadException(String message) {
        super(message, "FILE_UPLOAD_ERROR");
    }
    
    public FileUploadException(String message, Throwable cause) {
        super(message, "FILE_UPLOAD_ERROR", cause);
    }
}
