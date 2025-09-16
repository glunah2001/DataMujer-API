package com.UNED.APIDataMujer.controller;

import com.UNED.APIDataMujer.service.registration.ActivationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController con el proposition exclusivo de activar las cuentas de los usuarios
 * @author glunah2001
 * @see ActivationService
 * */
@RestController
@RequiredArgsConstructor
@RequestMapping("/activate")
public class ActivationController {

    private final ActivationService activationService;

    @GetMapping
    public ResponseEntity<?> activateAccount(@RequestParam final String token){
        activationService.activateAccount(token);
        return ResponseEntity.ok("Cuenta activada exitosamente. El inicio de sesión se ha autorizado");
    }

}
