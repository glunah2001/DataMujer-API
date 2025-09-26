package com.UNED.APIDataMujer.service.authentication;

import com.UNED.APIDataMujer.dto.authentication.UserLoginDTO;
import com.UNED.APIDataMujer.dto.token.TokenResponse;
import com.UNED.APIDataMujer.entity.*;
import com.UNED.APIDataMujer.enums.TokenType;
import com.UNED.APIDataMujer.exception.InvalidTokenException;
import com.UNED.APIDataMujer.exception.NotActiveUserException;
import com.UNED.APIDataMujer.exception.ResourceNotFoundException;
import com.UNED.APIDataMujer.repository.*;
import com.UNED.APIDataMujer.service.jwt.JwtService;
import com.UNED.APIDataMujer.service.registration.ActivationService;
import com.UNED.APIDataMujer.service.resource.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Clase de implementación de la interfaz que permite a los usuarios activos
 * autentificarse. También es la encargada de ofrecer JWT a los usuarios
 * mediante su JWT de refresco cuando el de acceso esté a punto de caducar.
 *
 * @author glunah2001
 * @see AuthService
 * */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final JwtService jwtService;
    private final ActivationService activationService;
    private final TokenService tokenService;

    private final AuthenticationManager authManager;
    private final UserRepository userRepository;

    /**
     * Permite el inicio de sesión a los usuarios activos.
     * @param userLoginDTO Información de inicio de sesión (username y contraseña).
     * @return Un TokenResponse que contiene un accessToken y un refreshToken nuevos.
     * @throws UsernameNotFoundException en caso de que el usuario no sea encontrado
     * */
    @Override
    public TokenResponse login(final UserLoginDTO userLoginDTO) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userLoginDTO.username(),
                        userLoginDTO.password()
                )
        );
        var user = userRepository.findByUsername(userLoginDTO.username())
                .orElseThrow(() ->
                        new UsernameNotFoundException("El usuario no se encuentra registrado en el sistema."));

        isUserActive(user);

        return tokenGeneration(user);
    }

    /**
     * Permite el refresco del token de acceso mediante el refreshToken.
     * @param authHeader header AUTHORIZATION del request http POST que lleva el Bearer token.
     * @return Un TokenResponse que contiene un accessToken y un refreshToken nuevos.
     * @throws IllegalArgumentException en caso de que el token sea inválido o caducado.
     * */
    @Override
    public TokenResponse refresh(final String authHeader) {
        if(authHeader == null || !authHeader.startsWith("Bearer "))
            throw new IllegalArgumentException("Formato de token inválido.");

        String token = authHeader.substring(7);
        var username = jwtService.getUsername(token);
        if(username == null)
            throw new IllegalArgumentException("El token de refresco es inválido: Nombre de usuario inválido");

        var user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException("El usuario indicado en el JWT no se haya registrado."));

        if(!jwtService.isTokenValid(token, user))
            throw new InvalidTokenException("El token proporcionado es inválido: " +
                    "Puede no pertenecer a su usuario o haber concluido su vida útil.");

        return tokenGeneration(user);
    }

    /**
     * Función auxiliar. Se usa para verificar que el usuario que intenta iniciar
     * sesión se encuentre activo para abordar los últimos pasos de la autentificación.
     * @param user verifica su estado.
     * @throws NotActiveUserException en caso de que el usuario no esté activo
     * */
    private void isUserActive(User user){
        if(user.isActive()) return;
        activationService.generateActivationToken(user);
        throw new NotActiveUserException("El usuario ha proporcionado las credenciales " +
                "correctas pero no ha autentificado su cuenta. " +
                "Por favor, active su cuenta mediante el correo enviado.");
    }

    /**
     * Función auxiliar. Se usa para generar los tokens de acceso y refresco del user
     * autenticado.
     * @param user se necesita el usuario para conocer quien es el propietarios de los
     *             tokens en la BD.
     * @return TokenResponse con el accessToken y refreshToken.
     * */
    private TokenResponse tokenGeneration(final User user){
        final var accessToken = jwtService.generateAccessToken(user);
        final var refreshToken = jwtService.generateRefreshToken(user);
        tokenService.revokeAllActiveTokens(user);
        tokenService.saveToken(accessToken, user, TokenType.BEARER);
        return new TokenResponse(accessToken, refreshToken);
    }
}
