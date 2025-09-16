package com.UNED.APIDataMujer.security.filter;

import com.UNED.APIDataMujer.dto.ApiError;
import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.mapper.ApiErrorMapper;
import com.UNED.APIDataMujer.repository.TokenRepository;
import com.UNED.APIDataMujer.repository.UserRepository;
import com.UNED.APIDataMujer.service.jwt.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

/**
 * Clase encargada de filtrar todas las peticiones. Tiene mayor enfasis en aquellas
 * dirigidas a rutas protegidas
 * @author glunah2001
 * */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final ApiErrorMapper apiErrorMapper;
    private final ObjectMapper objectMapper;

    /**
     * Función de clase abstracta encargada de revisar que cada request tenga un
     * JWT (su mecanismo de autentificación y autorización) sea válido para
     * colocar al usuario en el securityContext.
     * @param request petición enviada desde el cliente
     * @param response respuesta a enviar al cliente
     * @param filterChain secuencia de filtros a ejecutar
     * */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if(request.getServletPath().contains("/auth") || request.getServletPath().contains("/register") ||
                request.getServletPath().contains("/activate")){
            filterChain.doFilter(request, response);
            return;
        }

        final var header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(header == null || !header.startsWith("Bearer ")){
            sendError(response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Acceso no autorizado: formato de token inválido",
                    request.getServletPath());
            return;
        }

        final var jjwt = header.substring(7);
        final var username = jwtService.getUsername(jjwt);
        if(username == null){
            sendError(response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Acceso no autorizado: el token puede estar corrupto o ser inválido.",
                    request.getServletPath());
            return;
        }

        if(SecurityContextHolder.getContext().getAuthentication() != null){
            filterChain.doFilter(request, response);
            return;
        }

        final var token = tokenRepository.findByToken(jjwt).orElse(null);
        if(token == null || token.isExpired() || token.isRevoked()){
            sendError(response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Acceso no autorizado: el token ya no es válido.",
                    request.getServletPath());
            return;
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        final Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
        if(user.isEmpty()){
            sendError(response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Acceso no autorizado: el usuario indicado en el token " +
                            "no existe en la base de datos.",
                    request.getServletPath());
            return;
        }

        final boolean isTokenValid = jwtService.isTokenValid(jjwt, user.get());
        if(!isTokenValid){
            sendError(response,
                    HttpServletResponse.SC_UNAUTHORIZED,
                    "Acceso no autorizado: el token ya no es válido para este usuario.",
                    request.getServletPath());

            return;
        }

        final var authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
        );

        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }

    /**
     * Función auxiliar para retornar un DTO de reporte de error similar a como se hace con el
     * RestControllerAdvice general.
     * */
    private void sendError(HttpServletResponse response, int status, String message, String path)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");

        ApiError apiError = apiErrorMapper.toDto(
                HttpStatus.valueOf(status),
                message,
                path
        );

        response.getWriter().write(objectMapper.writeValueAsString(apiError));
    }
}
