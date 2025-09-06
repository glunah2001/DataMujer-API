package com.UNED.APIDataMujer.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> illegalArgumentHandler(IllegalArgumentException ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> usernameNotFoundHandler(UsernameNotFoundException ex){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, List<String>>> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        Map<String, List<String>> responseBody = new HashMap<>();
        responseBody.put("errors", errors);

        return ResponseEntity.badRequest().body(responseBody);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Throwable cause = ex.getCause();

        if (cause instanceof ConstraintViolationException cve) {
            String message = cve.getSQLException().getMessage();

            String fieldName = ConstraintUtils.extractFieldFromMessage(message);
            String friendlyField = fieldName != null ? fieldName : "valor único";

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("El valor para el campo \"" + friendlyField + "\" ya se ha registrado antes.");
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Error de integridad de datos: " + ex.getMessage());
    }
}
