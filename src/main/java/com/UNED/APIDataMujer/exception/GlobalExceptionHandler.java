package com.UNED.APIDataMujer.exception;

import com.UNED.APIDataMujer.dto.ApiError;
import com.UNED.APIDataMujer.mapper.ApiErrorMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

/**
 * Clase encargada del captado de excepciones a lo largo del código y que sean recibidas y
 * procesadas por el cliente (desktop app)
 * @author AHKolodin
 * @see ApiErrorMapper
 *
 * El parametro request hace referencia a la petición enviada del cliente al servidor. Se aclara
 * desde acá porque se repite mucho a lo largo del RestControllerAdvice
 * */
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ApiErrorMapper apiErrorMapper;

    /**
     * Clase encargada de manejar la excepción personalizada NotActiveUser. Estas se emiten
     * cuando un usuario con una cuenta inactiva intenta iniciar sesión.
     * @param ex la excepción en cuestión.
     * @return un dto. Con los detalles del error.
     * */
    @ExceptionHandler(NotActiveUserException.class)
    public ResponseEntity<ApiError> NotActiveUserHandler(NotActiveUserException ex, HttpServletRequest request){
        ApiError error = apiErrorMapper.toDto(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(error);
    }

    /**
     * Clase encargada de manejar excepciones IllegalArgument. Estas se emiten cuando un token o
     * algún dato necesario para una operación es inválido según la lógica de negocio.
     * @param ex la excepción en cuestión.
     * @return un dto. Con los detalles del error.
     * */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> illegalArgumentHandler(IllegalArgumentException ex, HttpServletRequest request){
        ApiError error = apiErrorMapper.toDto(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request.getRequestURI());

        return ResponseEntity
                .badRequest()
                .body(error);
    }

    /**
     * Clase encargada de manejar excepciones UsernameNotFound. Estas se emiten cuando un usuario
     * no se encuentra en la BD.
     * @param ex la excepción en cuestión.
     * @return un dto. Con los detalles del error.
     * */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> usernameNotFoundHandler(UsernameNotFoundException ex, HttpServletRequest request){
        ApiError error = apiErrorMapper.toDto(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request.getRequestURI());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    /**
     * Clase encargada de manejar excepciones Authentication. Estas se emiten cuando un usuario
     * ingresa credenciales de acceso incorrectas en el login.
     * @param ex la excepción en cuestión.
     * @return un dto. Con los detalles del error.
     * */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> authenticationExceptioHandler(AuthenticationException ex, HttpServletRequest request){
        ApiError error = apiErrorMapper.toDto(
                HttpStatus.UNAUTHORIZED,
                ex.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(error);
    }

    /**
     * Clase encargada de manejar excepciones MethodArgumentNotValid. Estas se emiten cuando
     * una persona enviá datos de actualización o registro no válidos que deben ser corregidos
     * antes de admitirse.
     * @param ex la excepción en cuestión.
     * @return un dto. Con los detalles del error con detalles sobre qué datos y por qué deben corregirse.
     * */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationErrors(MethodArgumentNotValidException ex,
                                                                            HttpServletRequest request) {
        List<String> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> {
                    String fullField = error.getField();
                    String[] parts = fullField.split("\\.");
                    String fieldName = parts[parts.length - 1];
                    return fieldName + ": " + error.getDefaultMessage();
                })
                .toList();

        ApiError error = apiErrorMapper.toDto(
                HttpStatus.BAD_REQUEST,
                "Error de validación",
                request.getRequestURI(),
                details);

        return ResponseEntity.badRequest().body(error);
    }

    /**
     * Clase encargada de manejar excepciones NotActiveUser. Estas se emiten cuando un usuario
     * trata de actualizar o ingresar datos como username, email o identificaciones (datos únicos en la bd)
     * que ya se encuentran registrados.
     * @param ex la excepción en cuestión.
     * @return un dto. Con los detalles del error.
     * */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(DataIntegrityViolationException ex, HttpServletRequest request) {
        Throwable cause = ex.getCause();
        String message;
        HttpStatus status;

        if (cause instanceof ConstraintViolationException cve) {
            String fieldName = ConstraintUtils.extractFieldFromMessage(cve.getSQLException().getMessage());
            fieldName = fieldName != null ? fieldName : "valor único";
            message = "El valor para el campo '" + fieldName + "' ya se ha registrado antes.";
            status = HttpStatus.CONFLICT;
        } else {
            message = "Error de integridad de datos: " + ex.getMessage();
            status = HttpStatus.BAD_REQUEST;
        }

        ApiError error = apiErrorMapper.toDto(
                status,
                message,
                request.getRequestURI());

        return ResponseEntity.status(status).body(error);
    }
}
