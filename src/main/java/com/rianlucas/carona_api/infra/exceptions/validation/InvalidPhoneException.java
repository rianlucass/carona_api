package com.rianlucas.carona_api.infra.exceptions.validation;

public class InvalidPhoneException extends RuntimeException {
    public InvalidPhoneException(String message) {
        super(message);
    }
}
