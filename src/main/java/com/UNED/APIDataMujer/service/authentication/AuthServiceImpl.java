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

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final JwtService jwtService;
    private final ActivationService activationService;

    private final AuthenticationManager authManager;
    private final TokenMapper tokenMapper;

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

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

    private void isUserActive(User user){
        if(user.isActive()) return;
        revokeAllUserToken(user);
        activationService.generateActivationToken(user);
        throw new NotActiveUserException("El usuario ha proporcionado las credenciales correctas pero no ha autentificado " +
                "su cuenta. Por favor, active su cuenta mediante el correo enviado.");
    }

    private TokenResponse tokenGeneration(final User user){
        final var accessToken = jwtService.generateAccessToken(user);
        final var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserToken(user);
        saveUserToken(accessToken, user);
        return new TokenResponse(accessToken, refreshToken);
    }

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

    private void saveUserToken(final String jjwt, final User user){
        final var token = tokenMapper.toEntity(jjwt, user, TokenType.BEARER);
        tokenRepository.save(token);
    }
}
