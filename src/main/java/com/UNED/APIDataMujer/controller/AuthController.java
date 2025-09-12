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

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthServiceImpl authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody final UserLoginDTO loginDTO){
        final TokenResponse token = authService.login(loginDTO);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader (HttpHeaders.AUTHORIZATION) final String authHeader){
        final TokenResponse token = authService.refresh(authHeader);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody final String email){
        passwordResetService.forgotPassword(email);
        return ResponseEntity.ok("Si el correo se encuentra registrado, se ha enviado un correo de " +
                "recuperación de contraseña.");
    }

    @PostMapping("reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody final ResetPasswordDTO resetPasswordDTO){
        passwordResetService.resetPassword(resetPasswordDTO);
        return ResponseEntity.ok("Contraseña restablecida con éxito");
    }

}
