package com.UNED.APIDataMujer.service.registration;

import com.UNED.APIDataMujer.entity.Token;
import com.UNED.APIDataMujer.entity.User;
import com.UNED.APIDataMujer.enums.TokenType;
import com.UNED.APIDataMujer.mapper.TokenMapper;
import com.UNED.APIDataMujer.repository.TokenRepository;
import com.UNED.APIDataMujer.repository.UserRepository;
import com.UNED.APIDataMujer.service.emailing.EmailSendingService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Clase que se encarga a lo relacionado a la activación de cuentas de los usuarios.
 * Aunque esta operación no es exclusiva del proceso de registro, bajo situaciones
 * normales solo se dará durante este proceso.
 * @author glunah2001
 * */
@Service
@RequiredArgsConstructor
public class ActivationService {

    private final TokenMapper tokenMapper;

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    private final EmailSendingService emailSendingService;

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

        String[] parts = token.getToken().split("_");
        long expiration = Long.parseLong(parts[1]);

        if(Instant.now().toEpochMilli() > expiration){
            revokeToken(token);
            throw new IllegalArgumentException("Este token de restablecimiento de contraseña ha caducado.");
        }

        User user = token.getUser();
        user.setActive(true);
        userRepository.save(user);

        token.setExpired(true);
        token.setRevoked(true);
        tokenRepository.save(token);
    }

    /**
     * Función inicial del proceso de activación de cuentas. Se encarga de crear el
     * token de activación y generar un correo que redirija al usuario a
     * dicha operación.
     * @param user El usuario cuya cuenta está inactiva y se le debe ejecutar esta
     *             operación.
     * */
    public void generateActivationToken(final User user) {
        long expiration = Instant.now().plus(1, ChronoUnit.DAYS).toEpochMilli();
        String tokenValue = UUID.randomUUID().toString() +"_"+expiration;
        Token token = tokenMapper.toEntity(tokenValue, user, TokenType.ACTIVATION);
        tokenRepository.save(token);

        String activationLink = "https://localhost:8443/activate?token=" + tokenValue;
        String body = String.format("Nos alegra mucho que te unas a Data Mujer. Por favor, " +
                "activa tu cuenta desde <a href=%s>este link</a>.", activationLink);

        emailSendingService.sendEmail(user.getEmail(),
                "Activación de Cuenta",
                body, user.getUsername());
    }

    /**
     * Function auxiliar. Se encarga de revocar el token de activación de cuenta.
     * Esta operación no hace rollback bajo excepción controlada.
     * */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void revokeToken(Token token) {
        token.setExpired(true);
        token.setRevoked(true);
        tokenRepository.save(token);
    }
}
