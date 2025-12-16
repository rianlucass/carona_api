package com.rianlucas.carona_api.infra.exceptions.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.rianlucas.carona_api.domain.response.ErrorResponse;
import com.rianlucas.carona_api.infra.exceptions.BusinessException;
import com.rianlucas.carona_api.infra.exceptions.auth.EmailNotVerifiedException;
import com.rianlucas.carona_api.infra.exceptions.auth.InvalidCredentialsException;
import com.rianlucas.carona_api.infra.exceptions.auth.TokenGenerationException;
import com.rianlucas.carona_api.infra.exceptions.auth.TokenValidationException;
import com.rianlucas.carona_api.infra.exceptions.user.EmailAlreadyExistsException;
import com.rianlucas.carona_api.infra.exceptions.user.PhoneAlreadyExistsException;
import com.rianlucas.carona_api.infra.exceptions.user.UserNotFoundException;
import com.rianlucas.carona_api.infra.exceptions.user.UsernameAlreadyExistsException;
import com.rianlucas.carona_api.infra.exceptions.validation.InvalidDateFormatException;
import com.rianlucas.carona_api.infra.exceptions.verification.EmailSendingException;
import com.rianlucas.carona_api.infra.exceptions.verification.InvalidVerificationCodeException;
import com.rianlucas.carona_api.infra.exceptions.verification.VerificationCodeExpiredException;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler global de exceptions para tratamento centralizado de erros
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ==================== AUTHENTICATION EXCEPTIONS ====================
    
    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<ErrorResponse> handleEmailNotVerified(
            EmailNotVerifiedException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            ex.getMessage(), 
            ex.getErrorCode(), 
            request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            ex.getMessage(), 
            ex.getErrorCode(), 
            request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse("Email ou senha inválidos.", "AUTH_002", request.getDescription(false).replace("uri=", ""));
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ErrorResponse> handleDisabledAccount(
            DisabledException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            "Conta desabilitada. Por favor, verifique seu email.", 
            "AUTH_001", 
            request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
    }

    @ExceptionHandler(TokenGenerationException.class)
    public ResponseEntity<ErrorResponse> handleTokenGeneration(
            TokenGenerationException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            ex.getMessage(), 
            ex.getErrorCode(), 
            request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(TokenValidationException.class)
    public ResponseEntity<ErrorResponse> handleTokenValidation(
            TokenValidationException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            ex.getMessage(), 
            ex.getErrorCode(), 
            request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    // ==================== USER EXCEPTIONS ====================

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(
            EmailAlreadyExistsException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            ex.getMessage(), 
            ex.getErrorCode(), 
            request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUsernameAlreadyExists(
            UsernameAlreadyExistsException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            ex.getMessage(), 
            ex.getErrorCode(), 
            request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(PhoneAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handlePhoneAlreadyExists(
            PhoneAlreadyExistsException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            ex.getMessage(), 
            ex.getErrorCode(), 
            request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            ex.getMessage(), 
            ex.getErrorCode(), 
            request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    // ==================== VERIFICATION EXCEPTIONS ====================

    @ExceptionHandler(InvalidVerificationCodeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidVerificationCode(
            InvalidVerificationCodeException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            ex.getMessage(), 
            ex.getErrorCode(), 
            request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(VerificationCodeExpiredException.class)
    public ResponseEntity<ErrorResponse> handleVerificationCodeExpired(
            VerificationCodeExpiredException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            ex.getMessage(), 
            ex.getErrorCode(), 
            request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<ErrorResponse> handleEmailSending(
            EmailSendingException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            ex.getMessage(), 
            ex.getErrorCode(), 
            request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    // ==================== VALIDATION EXCEPTIONS ====================

    @ExceptionHandler(InvalidDateFormatException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDateFormat(
            InvalidDateFormatException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            ex.getMessage(), 
            ex.getErrorCode(), 
            request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "Erro de validação nos campos");
        response.put("errorCode", "VALIDATION_ERROR");
        response.put("errors", errors);
        response.put("path", request.getDescription(false).replace("uri=", ""));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // ==================== GENERIC EXCEPTIONS ====================

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            ex.getMessage(), 
            ex.getErrorCode(), 
            request.getDescription(false).replace("uri=", "")
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        ErrorResponse error = new ErrorResponse(
            "Erro interno do servidor. Tente novamente mais tarde.", 
            "INTERNAL_SERVER_ERROR", 
            request.getDescription(false).replace("uri=", "")
        );
        // Log do erro real para debug
        ex.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
