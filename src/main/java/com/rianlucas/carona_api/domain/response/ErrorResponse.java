package com.rianlucas.carona_api.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private boolean success = false;
    private String message;
    private String errorCode;
    private LocalDateTime timestamp;
    private String path;
    
    public ErrorResponse(String message, String errorCode, String path) {
        this.message = message;
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
        this.path = path;
    }
}
