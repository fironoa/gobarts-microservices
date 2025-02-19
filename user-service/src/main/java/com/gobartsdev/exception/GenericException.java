package com.gobartsdev.exception;

import com.gobartsdev.model.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GenericException {
    // Handle all exceptions (generic handler)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex) {
        ex.printStackTrace();
        return generateException(ex.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(JwtAuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleJwtAuthException(JwtAuthenticationException ex){
        return generateException(ex.getLocalizedMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex){
        return generateException(ex.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAuthorizationDeniedException(Exception ex) {
        return generateException(ex.getLocalizedMessage(), HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFoundException(AuthenticationException ex){
        return generateException(ex.getLocalizedMessage(), HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<ErrorResponse> generateException(String message, HttpStatus status){
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .message(message)
                        .timestamp(Instant.now())
                        .build(), status
        );
    }

}
