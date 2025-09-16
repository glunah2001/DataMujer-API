package com.UNED.APIDataMujer.service.registration;

import com.UNED.APIDataMujer.entity.Token;
import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.enums.TokenType;
import com.UNED.APIDataMujer.repository.TokenRepository;
import com.UNED.APIDataMujer.repository.UserRepository;
import com.UNED.APIDataMujer.service.emailing.EmailSendingService;

import com.UNED.APIDataMujer.service.resource.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Clase que se encarga a lo relacionado a la activación de cuentas de los usuarios.
 * Aunque esta operación no es exclusiva del proceso de registro, bajo situaciones
 * normales solo se dará durante este proceso.
 * @author glunah2001
 * */
@Service
@RequiredArgsConstructor
public class ActivationService {
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    private final EmailSendingService emailSendingService;
    private final TokenService tokenService;

    /**
     * Función principal del proceso. Es la función encargada de ejecutar la activación
     * del perfil.
     * @param tokenValue se trata del token de activación enviado al correo del usuario.
     * @throws IllegalArgumentException en caso de que el token de activación
     * sea inválido o expirado.
     * */
    @Transactional
    public void activateAccount(final String tokenValue) {
        Token token = tokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new IllegalArgumentException("Su token de activación es inexistente."));

        if(token.isExpired() || token.isRevoked()){
            throw new IllegalArgumentException("Este token de activación ha caducado.");
        }

        if(tokenService.isTokenExpired(token)){
            tokenService.revokeToken(token);
            throw new IllegalArgumentException("Este token de activación ha caducado.");
        }

        User user = token.getUser();
        user.setActive(true);
        userRepository.save(user);

        tokenService.revokeToken(token);
    }

    /**
     * Función inicial del proceso de activación de cuentas. Se encarga de crear el
     * token de activación y generar un correo que redirija al usuario a
     * dicha operación.
     * @param user El usuario cuya cuenta está inactiva y se le debe ejecutar esta
     *             operación.
     * */
    public void generateActivationToken(final User user) {
        tokenService.revokeAllActiveTokens(user);
        long expiration = 24 * 60 * 60 * 1000;
        final String activationToken = tokenService.generateToken(expiration);
        tokenService.saveToken(activationToken, user, TokenType.ACTIVATION);
        String activationLink = "https://localhost:8443/activate?token=" + activationToken;
        emailSendingService.sendEmail(user.getEmail(),
                "Activa tu Cuenta",
                "Haz click en el enlace para activar su cuenta: "+activationLink);
    }
}
