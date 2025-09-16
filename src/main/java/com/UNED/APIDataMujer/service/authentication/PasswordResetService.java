package com.UNED.APIDataMujer.service.authentication;

import com.UNED.APIDataMujer.dto.authentication.ResetPasswordDTO;
import com.UNED.APIDataMujer.entity.Token;
import com.UNED.APIDataMujer.enums.TokenType;
import com.UNED.APIDataMujer.mapper.TokenMapper;
import com.UNED.APIDataMujer.repository.TokenRepository;
import com.UNED.APIDataMujer.repository.UserRepository;
import com.UNED.APIDataMujer.service.emailing.EmailSendingService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

/**
 * Esta clase es la encargada del restablecimiento de contraseña por parte de los usuarios
 * @author AHKolodin
 * */
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    private final TokenMapper tokenMapper;

    private final EmailSendingService emailSendingService;

    private final PasswordEncoder passwordEncoder;

    /**
     * Función inicial del proceso. Encargada de encontrar el usuario con contraseña
     * a restablecer mediante su correo registrado. Envía un correo a dicha dirección
     * con el procedimiento para restablecimiento de Contraseña.
     * @param email dirección electronica que debe estar registrada para enviar un
     *              token de recuperación de contraseña que se usará
     *              en el desktop app.
     * @throws UsernameNotFoundException en caso de que el email no se encuentre registrado
     * */
    public void forgotPassword(final String email){
        final var user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("El email: "+email+" no se encuentra registrado a " +
                        "ningún usuario actualmente."));

        long expiration = Instant.now().plus(15, ChronoUnit.MINUTES).toEpochMilli();
        final String resetToken = UUID.randomUUID() +"_"+ expiration;
        final Token token = tokenMapper.toEntity(resetToken, user, TokenType.PASSWORD_RESET);
        tokenRepository.save(token);

        String message = String.format("Este es su token de restablecimiento de contraseña: %s.\n" +
                "Ingrese dicho token en el espacio indicado en la aplicación junto a su nueva contraseña " +
                "para hacer efectivo el cambio.",resetToken);

        emailSendingService.sendEmail(email, "Restablecimiento de Contraseña", message);
    }

    /**
     * Esta función es la encargada de hacer el reseteo de la contraseña, reemplazando
     * el valor antiguo por una nueva clave de acceso.
     * @param dto Un dto. Que contiene el token enviado por correo y que se encuentra
     *            registrado; y la nueva contraseña.
     * @throws IllegalArgumentException en caso de que el token de restablecimiento
     * sea inválido o caducado
     * */
    public void resetPassword(final ResetPasswordDTO dto){
        final var token = tokenRepository.findByToken(dto.token())
                .orElseThrow(() ->
                        new IllegalArgumentException("El token ingresado no es válido"));

        if(token.isExpired() || token.isRevoked()){
            throw new IllegalArgumentException("Este token de restablecimiento de contraseña ha caducado.");
        }

        String[] parts = token.getToken().split("_");
        long expiration = Long.parseLong(parts[1]);

        if(Instant.now().toEpochMilli() > expiration){
            revokeToken(token);
            throw new IllegalArgumentException("Este token de restablecimiento de contraseña ha caducado.");
        }

        var user = token.getUser();
        user.setPassword(passwordEncoder.encode(dto.password()));
        userRepository.save(user);
        revokeToken(token);
    }

    /**
     * Función auxiliar. Se encarga de revocar el token de recuperación de contraseña.
     * Esta función no hace rollback ante una excepción controlada.
     * @param token token de reset utilizado para la operación.
     * */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private void revokeToken(Token token) {
        token.setExpired(true);
        token.setRevoked(true);
        tokenRepository.save(token);
    }
}
