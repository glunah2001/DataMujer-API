package com.UNED.APIDataMujer.security.filter;

import com.UNED.APIDataMujer.dto.ApiError;
import com.UNED.APIDataMujer.mapper.ApiErrorMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

/**
 * Clase encargada de filtrar todas las peticiones según su versión de cliente.
 * @author glunah2001
 * */
@Component
@RequiredArgsConstructor
public class VersionCheckFilter extends OncePerRequestFilter {

    private static final Map<String, String> MIN_SUPPORTED_VERSIONS = Map.of(
            "DataMujer-Client", "1.1.0"
    );

    private final ApiErrorMapper apiErrorMapper;
    private final ObjectMapper objectMapper;

    /**
     * Función de clase abstracta encargada de revisar que cada request se emita
     * desde un cliente cuya versión esté soportada.
     * @param request petición enviada desde el cliente
     * @param response respuesta a enviar al cliente
     * @param filterChain secuencia de filtros a ejecutar
     * */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if(request.getServletPath().contains("/activate")){
            filterChain.doFilter(request, response);
            return;
        }

        String clientName = request.getHeader("X-Client-Name");
        String clientVersion = request.getHeader("X-Client-Version");

        if(clientName == null || clientVersion == null){
            sendError(
                    response,
                    "Acceso no autorizado: formato de cliente inválido",
                    request.getServletPath()
            );
            return;
        }

        if(clientName.isBlank() || clientVersion.isBlank()){
            sendError(
                    response,
                    "Acceso no autorizado: formato de cliente inválido",
                    request.getServletPath()
            );
            return;
        }

        String minVersion = MIN_SUPPORTED_VERSIONS.get(clientName);

        if(minVersion == null){
            sendError(
                    response,
                    "Acceso no autorizado: Cliente inválido",
                    request.getServletPath()
            );
            return;
        }

        if(isOlderVersion(clientVersion, minVersion)){
            sendError(
                    response,
                    "Acceso no autorizado: Su cliente pertenece a una versión antigua. Actualice a la versión "+minVersion+".",
                    request.getServletPath()
            );
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isOlderVersion(String current, String minimum) {
        String[] currentParts = current.split("\\.");
        String[] minimumParts = minimum.split("\\.");

        for (int i = 0; i < Math.max(currentParts.length, minimumParts.length); i++) {
            int c = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;
            int m = i < minimumParts.length ? Integer.parseInt(minimumParts[i]) : 0;
            if (c < m) return true;
            if (c > m) return false;
        }
        return false;
    }

    private void sendError(HttpServletResponse response, String message, String path)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json");

        ApiError apiError = apiErrorMapper.toDto(
                HttpStatus.valueOf(HttpServletResponse.SC_FORBIDDEN),
                message,
                path
        );

        response.getWriter().write(objectMapper.writeValueAsString(apiError));
    }
}
