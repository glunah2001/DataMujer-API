package com.UNED.APIDataMujer.service.authentication;

import com.UNED.APIDataMujer.dto.authentication.UserLoginDTO;
import com.UNED.APIDataMujer.dto.token.TokenResponse;
import com.UNED.APIDataMujer.entity.*;
import com.UNED.APIDataMujer.enums.TokenType;
import com.UNED.APIDataMujer.exception.NotActiveUserException;
import com.UNED.APIDataMujer.mapper.TokenMapper;
import com.UNED.APIDataMujer.repository.*;
import com.UNED.APIDataMujer.service.jwt.JwtService;
import com.UNED.APIDataMujer.service.registration.ActivationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * Clase de implementación de la interfaz que permite a los usuarios activos
 * autentificarse. Tambien es la encargada de ofrecer JWT a los usuarios
 * mediante su JWT de refresco cuando el de acceso esté apunto de caducar.
 *
 * @author glunah2001
 * @see AuthService
 * */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final JwtService jwtService;
    private final ActivationService activationService;

    private final AuthenticationManager authManager;
    private final TokenMapper tokenMapper;

    private final TokenRepository tokenRepository;
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
                        new UsernameNotFoundException("El usuario no se encuentra registrado en el sistema."));

        if(!jwtService.isTokenValid(token, user))
            throw new IllegalArgumentException("El token de refresco es inválido.");

        return tokenGeneration(user);
    }

    /**
     * Función auxiliar. Se usa para verificar que el usuario que intenta iniciar.
     * sesión se encuentre activo para abordar los últimos pasos de la autentificación.
     * @param user verifica su estado.
     * @throws NotActiveUserException en caso de que el usuario no esté activo
     * */
    private void isUserActive(User user){
        if(user.isActive()) return;
        revokeAllUserToken(user);
        activationService.generateActivationToken(user);
        throw new NotActiveUserException("El usuario ha proporcionado las credenciales " +
                "correctas pero no ha autentificado su cuenta. " +
                "Por favor, active su cuenta mediante el correo enviado.");
    }

    /**
     * Función auxiliar. Se usa para generar los tokens de acceso y refresco del user
     * autenticado.
     * @param user se necesita el usuario para conocer quien es el propietarios de los
     * tokens en la BD.
     * @return TokenResponse con el accessToken y refreshToken.
     * */
    private TokenResponse tokenGeneration(final User user){
        final var accessToken = jwtService.generateAccessToken(user);
        final var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserToken(user);
        saveUserToken(accessToken, user);
        return new TokenResponse(accessToken, refreshToken);
    }

    /**
     * Función auxiliar. Se usa para obtener todos los tokens que no se han suspendido
     * del usuario para revocarlos.
     * @param user se trata del usuario a quien hay que suspenderle tokens caducados.
     * */
    private void revokeAllUserToken(final User user){
        final List<Token> tokens = tokenRepository
                .findAllValidIsFalseOrRevokedIsFalseByUserId(user.getId());

        if(tokens.isEmpty()) return;

        tokens.forEach(token -> {
            token.setRevoked(true);
            token.setExpired(true);
        });
        tokenRepository.saveAll(tokens);
    }

    /**
     * Función auxiliar. Se usa para un nuevo token de acceso en la base de datos.
     * @param jjwt contiene al token de acceso qye se almacenará en la bd
     * @param user el usuario al que pertenece el token.
     * */
    private void saveUserToken(final String jjwt, final User user){
        final var token = tokenMapper.toEntity(jjwt, user, TokenType.BEARER);
        tokenRepository.save(token);
    }
}
