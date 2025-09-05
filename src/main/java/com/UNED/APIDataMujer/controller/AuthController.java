package com.UNED.APIDataMujer.controller;

import com.UNED.APIDataMujer.dto.authentication.LegalPersonRegisterDTO;
import com.UNED.APIDataMujer.dto.authentication.PhysicalPersonRegisterDTO;
import com.UNED.APIDataMujer.service.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthServiceImpl authService;

    @GetMapping("/register-physical")
    public ResponseEntity<?> register(@Valid @RequestBody final PhysicalPersonRegisterDTO physicalPersonRegisterDTO){
        final var physicalPerson = authService.physicalRegister(physicalPersonRegisterDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/users/{username}")
                .buildAndExpand(physicalPerson.username())
                .toUri();
        return ResponseEntity.created(location).body("Usuario (Persona Física) registrado exitosamente");
    }

    @GetMapping("/register-legal")
    public ResponseEntity<?> register(@Valid @RequestBody final LegalPersonRegisterDTO legalPersonRegisterDTO){
        final var legalPerson = authService.legalRegister(legalPersonRegisterDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/users/{username}")
                .buildAndExpand(legalPerson.username())
                .toUri();
        return ResponseEntity.created(location).body("Usuario (Persona Jurídica) registrado exitosamente");
    }

    @GetMapping("/login")
    public ResponseEntity<?> login(){
        return ResponseEntity.ok(null);
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(){
        return ResponseEntity.ok(null);
    }

    /*@GetMapping("/test")
    public String refresh(){
        return "prueba";
    }*/
}
