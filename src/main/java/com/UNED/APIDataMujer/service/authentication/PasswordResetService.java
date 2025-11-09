package com.UNED.APIDataMujer.service.authentication;

import com.UNED.APIDataMujer.dto.authentication.ResetPasswordDTO;
import com.UNED.APIDataMujer.enums.TokenType;
import com.UNED.APIDataMujer.exception.BusinessValidationException;
import com.UNED.APIDataMujer.exception.InvalidTokenException;
import com.UNED.APIDataMujer.exception.ResourceNotFoundException;
import com.UNED.APIDataMujer.repository.TokenRepository;
import com.UNED.APIDataMujer.repository.UserRepository;
import com.UNED.APIDataMujer.service.emailing.EmailSendingService;
import com.UNED.APIDataMujer.service.resource.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Esta clase es la encargada del restablecimiento de contraseña por parte de los usuarios
 * @author AHKolodin
 * */
@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    private final EmailSendingService emailSendingService;
    private final TokenService tokenService;

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
        if(email == null)
            throw new BusinessValidationException("Usted no está proporcionando un email válido.");

        final var user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException("No se ha encontrado ningún usuario registrado con el " +
                                "email: "+email+".")
                );

        tokenService.revokeAllActiveTokens(user);

        long expiration = 15 * 60 * 1000;
        final String resetToken = tokenService.generateToken(expiration);
        tokenService.saveToken(resetToken, user, TokenType.PASSWORD_RESET);

        String message = String.format("""
                Este es su token de restablecimiento de contraseña:
                <br><br><b>%s</b><br><br>
                Ingrese dicho token en el espacio indicado en la aplicación junto con su nueva contraseña.
                Bajo ningún motivo comparta este token con ningúna otra persona.
                """, resetToken);

        emailSendingService.sendEmail(email,
                "Restablecimiento de Contraseña",
                message, user.getUsername());
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
                        new ResourceNotFoundException("El token ingresado no se ha encontrado en la base de datos."));

        if(token.isExpired() || token.isRevoked()){
            throw new InvalidTokenException("El token proporcionado es inválido: " +
                    "Se encuentra marcado como expirado o revocado.");
        }

        if(tokenService.isTokenExpired(token)){
            tokenService.revokeToken(token);
            throw new InvalidTokenException("El token proporcionado es inválido: " +
                    "Su tiempo de vida válido ha concluido.");
        }

        var user = token.getUser();
        user.setPassword(passwordEncoder.encode(dto.password()));
        userRepository.save(user);
        tokenService.revokeToken(token);;
    }

}
