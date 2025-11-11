package com.UNED.APIDataMujer.controller;

import com.UNED.APIDataMujer.dto.authentication.ResetPasswordDTO;
import com.UNED.APIDataMujer.dto.authentication.UserLoginDTO;
import com.UNED.APIDataMujer.dto.token.TokenResponse;
import com.UNED.APIDataMujer.service.authentication.AuthServiceImpl;
import com.UNED.APIDataMujer.service.authentication.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * RestController encargado de escuchar las operaciones relacionadas con la
 * autentificación de usuarios.
 * @author glunah2001
 * @see AuthServiceImpl
 * @see PasswordResetService
 * */
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthServiceImpl authService;
    private final PasswordResetService passwordResetService;

    /**
     * Función post que permite a los usuarios iniciar sesión.
     * @param loginDTO dto con la información de inicio de sesión de un usuario.
     * @return un dto. Con los tokens de acceso y refresco.
     * */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody final UserLoginDTO loginDTO){
        final TokenResponse token = authService.login(loginDTO);
        return ResponseEntity.ok(token);
    }

    /**
     * Función post que permite a los usuarios renovar su token de acceso.
     * @param authHeader contiene el token de refresco transportado desde el header AUTHORIZATION.
     * @return un dto. Con los nuevos tokens de acceso y refresco.
     * */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader (HttpHeaders.AUTHORIZATION) final String authHeader){
        final TokenResponse token = authService.refresh(authHeader);
        return ResponseEntity.ok(token);
    }

    /**
     * Función post que permite a los usuarios solicitar un cambio de contraseña en caso
     * de olvidarla.
     * @param email se trata del email en texto plano (no se manda en formato json, la capa extra en
     *              https lo mantiene seguro).
     * @return un estado 200 OK.
     * */
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestParam final String email){
        passwordResetService.forgotPassword(email);
        return ResponseEntity.ok("Si el correo se encuentra registrado, se ha enviado un correo de " +
                "recuperación de contraseña.");
    }

    /**
     * Función post que permite a los usuarios ejecutar un cambio de contraseña
     * @param resetPasswordDTO se trata de token de reset más la nueva contraseña encapsulados en
     *                         un dto.
     * @return un estado 200 OK.
     * */
    @PostMapping("reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody final ResetPasswordDTO resetPasswordDTO){
        passwordResetService.resetPassword(resetPasswordDTO);
        return ResponseEntity.ok("Contraseña restablecida con éxito");
    }

}
