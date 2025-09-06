package com.UNED.APIDataMujer.security.filter;

import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.repository.TokenRepository;
import com.UNED.APIDataMujer.repository.UserRepository;
import com.UNED.APIDataMujer.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if(request.getServletPath().contains("/auth") || request.getServletPath().contains("/register")){
            filterChain.doFilter(request, response);
            return;
        }

        final var header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(header == null || !header.startsWith("Bearer ")){
            response.setStatus((HttpServletResponse.SC_UNAUTHORIZED));
            response.getWriter().write("Acceso no autorizado: " +
                    "formato de token inválido");
            return;
        }

        final var jjwt = header.substring(7);
        final var username = jwtService.getUsername(jjwt);
        if(username == null){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Acceso no autorizado: " +
                    "el token puede ser corrupto o inválido.");
            return;
        }

        if(SecurityContextHolder.getContext().getAuthentication() != null){
            filterChain.doFilter(request, response);
            return;
        }

        final var token = tokenRepository.findByToken(jjwt).orElse(null);
        if(token == null || token.isExpired() || token.isRevoked()){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Acceso no autorizado: " +
                    "el token ya no es válido.");
            return;
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        final Optional<User> user = userRepository.findByUsername(userDetails.getUsername());
        if(user.isEmpty()){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Acceso no autorizado: " +
                    "el usuario indicado en el token no existe en la base de datos.");
            return;
        }

        final boolean isTokenValid = jwtService.isTokenValid(jjwt, user.get());
        if(!isTokenValid){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Acceso no autorizado: " +
                    "el token ya no es válido para este usuario.");
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
}
