package com.UNED.APIDataMujer.mapper;

import com.UNED.APIDataMujer.dto.ApiError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Clase Mapper encargada de mapear exclusivamente a DTO una excepción. Con el
 * objetivo de que el usuario pueda procesar dicho contratiempo.
 * @author AHKolodin
 * */
@Component
@RequiredArgsConstructor
public class ApiErrorMapper {

    /**
     * Mapeo manual a DTO de una excepción con detalles.
     * @param status estado de respuesta (400, 404, 500, etc)
     * @param message mensaje del error (Bad Request, Not FFound, etc)
     * @param path ruta de endpoint en la cual ocurre el error
     * @param details detalles del error (en caso de registro o actualización)
     * @return DTO de errores.
     * */
    public ApiError toDto(HttpStatus status, String message, String path, List<String> details){
        return new ApiError(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                details
        );
    }

    /**
     * Mapeo manual a DTO de una excepción sin detalles (solo se omiten y se manda a
     * llamar a la función anterior con details en NULL).
     * @param status estado de respuesta (400, 404, 500, etc)
     * @param message mensaje del error (Bad Request, Not FFound, etc)
     * @param path ruta de endpoint en la cual ocurre el error
     * @return DTO de errores.
     * */
    public ApiError toDto(HttpStatus status, String message, String path){
        return toDto(status, message, path, null);
    }

}
