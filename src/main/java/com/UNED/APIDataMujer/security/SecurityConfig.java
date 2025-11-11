package com.UNED.APIDataMujer.security;

import com.UNED.APIDataMujer.dto.ApiError;
import com.UNED.APIDataMujer.entity.Token;
import com.UNED.APIDataMujer.exception.ResourceNotFoundException;
import com.UNED.APIDataMujer.mapper.ApiErrorMapper;
import com.UNED.APIDataMujer.repository.TokenRepository;
import com.UNED.APIDataMujer.security.filter.JwtAuthFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

/**
 * Clase de configuración encargada de definir rutas públicas y privadas, permisos
 * necesarios, filtros de control y mecanismo de logout.
 * @author glunah2001
 * @see JwtAuthFilter
 * */
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig{

    private final AuthenticationProvider authProvider;
    private final JwtAuthFilter jwtAuthFilter;
    private final TokenRepository tokenRepository;
    private final ApiErrorMapper apiErrorMapper;
    private final ObjectMapper objectMapper;

    /**
     * Bean definido para definir varios aspectos de seguridad necesarios para la operabilidad
     * de la aplicación.
     * Según el flujo las request: son captadas por el securityFilterChain, llegan al JwtFilter,
     * son procesadas, si tienen éxito en su comprobación o son rutas públicas se resuelve el
     * securityFilterChain y se envía finalmente al endpoint.
     *
     * @param httpSecurity objeto de configuración principal de Spring Security
     * @return un objeto pre-configurado e inyectable SecurityFilterChain
     * @throws Exception una excepción genérica
     * */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/register/**", "/activate/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request,
                                                   response,
                                                   authException) ->
                                handleError(HttpStatus.UNAUTHORIZED,
                                        "No estás autenticado",
                                        request, response))
                        .accessDeniedHandler((request,
                                              response,
                                              accessDeniedException) ->
                                handleError(HttpStatus.FORBIDDEN,
                                        "No tienes permisos para realizar esta acción",
                                        request, response))
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .addLogoutHandler((request,
                                           response,
                                           authentication) -> {
                            try{
                                final var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
                                logout(authHeader);
                            }catch (IllegalArgumentException ex) {
                                try{
                                    handleError(HttpStatus.BAD_REQUEST, ex.getMessage(), request, response);
                                }catch (IOException ioEx) {
                                    throw new RuntimeException(ioEx);
                                }
                            }catch (ResourceNotFoundException ex) {
                                try{
                                    handleError(HttpStatus.NOT_FOUND, ex.getMessage(), request, response);
                                }catch (IOException ioEx) {
                                    throw new RuntimeException(ioEx);
                                }
                            }
                        })
                        .logoutSuccessHandler((request,
                                               response,
                                               authentication) ->
                                SecurityContextHolder.clearContext())
                )
                .build();
    }

    /**
     * Función que especifica el mecanismo de logout
     * @param authHeader Token JWT.
     * @throws IllegalArgumentException en caso de que el JWT sea inválido.
     * */
    private void logout(final String authHeader){
        if(authHeader == null || !authHeader.startsWith("Bearer "))
            throw new IllegalArgumentException("Token Bearer Inválido");

        final String jwtToken = authHeader.substring(7);
        final Token foundToken = tokenRepository.findByToken(jwtToken)
                .orElseThrow(() ->
                        new ResourceNotFoundException("El token ingresado no se ha " +
                                "encontrado en la base de datos."));
        foundToken.setRevoked(true);
        foundToken.setExpired(true);
        tokenRepository.save(foundToken);
    }

    /**Función auxiliar encargada de escribir y enviar errores en el handler
     * del SecurityFilterChain.
     * @param response respuesta a enviar al cliente
     * @param request request enviada por el cliente
     * @param status estatus del error
     * @param message mensaje del error
     * */
    private void handleError(HttpStatus status,
                             String message,
                             HttpServletRequest request,
                             HttpServletResponse response) throws IOException {
        ApiError error = apiErrorMapper.toDto(
                status,
                "Usted no tiene los permisos de acceso necesarios para realizar esta operación.",
                request.getServletPath()
        );
        response.setContentType("application/json");
        response.setStatus(status.value());
        response.getWriter().write(objectMapper.writeValueAsString(error));
    }
}
