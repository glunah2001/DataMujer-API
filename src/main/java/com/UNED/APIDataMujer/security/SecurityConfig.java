package com.UNED.APIDataMujer.security;

import com.UNED.APIDataMujer.entity.Token;
import com.UNED.APIDataMujer.repository.TokenRepository;
import com.UNED.APIDataMujer.security.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Bean de configuración encargada de definir rutas públicas y privadas, permisos
 * necesarios, filtros de control y mecanismo de logout.
 * @author glunah2001
 * @see JwtAuthFilter
 * */
@Configuration
@RequiredArgsConstructor
public class SecurityConfig{

    private final AuthenticationProvider authProvider;
    private final JwtAuthFilter jwtAuthFilter;
    private final TokenRepository tokenRepository;

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
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .addLogoutHandler((request,
                                           response,
                                           authentication) -> {
                            final var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
                            logout(authHeader);
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
                        new IllegalArgumentException("El token de Acceso proporcionado no está registrado"));
        foundToken.setRevoked(true);
        foundToken.setExpired(true);
        tokenRepository.save(foundToken);
    }
}
