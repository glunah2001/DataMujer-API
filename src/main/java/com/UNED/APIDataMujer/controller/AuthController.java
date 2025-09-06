package com.UNED.APIDataMujer.controller;

import com.UNED.APIDataMujer.dto.authentication.LegalPersonRegisterDTO;
import com.UNED.APIDataMujer.dto.authentication.PhysicalPersonRegisterDTO;
import com.UNED.APIDataMujer.dto.authentication.UserLoginDTO;
import com.UNED.APIDataMujer.dto.token.TokenResponse;
import com.UNED.APIDataMujer.service.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/register-physical")
    public ResponseEntity<?> register(@Valid @RequestBody final PhysicalPersonRegisterDTO physicalPersonRegisterDTO){
        final var physicalPerson = authService.physicalRegister(physicalPersonRegisterDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/users/{username}")
                .buildAndExpand(physicalPerson.username())
                .toUri();
        return ResponseEntity.created(location).body(physicalPerson);
    }

    @PostMapping("/register-legal")
    public ResponseEntity<?> register(@Valid @RequestBody final LegalPersonRegisterDTO legalPersonRegisterDTO){
        final var legalPerson = authService.legalRegister(legalPersonRegisterDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/users/{username}")
                .buildAndExpand(legalPerson.username())
                .toUri();
        return ResponseEntity.created(location).body(legalPerson);
    }

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
}
