package com.rianlucas.carona_api.infra.exceptions;

/**
 * Exception base para todas as exceptions personalizadas da aplicação
 */
public abstract class BusinessException extends RuntimeException {
    
    private final String errorCode;
    
    public BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public BusinessException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
