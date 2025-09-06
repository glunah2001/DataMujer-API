package com.UNED.APIDataMujer.mapper;

import com.UNED.APIDataMujer.dto.ApiError;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ApiErrorMapper {

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

    public ApiError toDto(HttpStatus status, String message, String path){
        return toDto(status, message, path, null);
    }

}
