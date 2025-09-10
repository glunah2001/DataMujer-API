package com.UNED.APIDataMujer.controller;

import com.UNED.APIDataMujer.dto.register.LegalPersonRegisterDTO;
import com.UNED.APIDataMujer.dto.register.PhysicalPersonRegisterDTO;
import com.UNED.APIDataMujer.service.UserRegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/register")
public class UserRegisterController {

    private final UserRegisterService userRegisterService;

    @PostMapping("/physical")
    public ResponseEntity<?> register(@Valid @RequestBody final PhysicalPersonRegisterDTO physicalPersonRegisterDTO){
        final var physicalPerson = userRegisterService.physicalRegister(physicalPersonRegisterDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/users/{username}")
                .buildAndExpand(physicalPerson.username())
                .toUri();
        return ResponseEntity.created(location).body(physicalPerson);
    }

    @PostMapping("/legal")
    public ResponseEntity<?> register(@Valid @RequestBody final LegalPersonRegisterDTO legalPersonRegisterDTO){
        final var legalPerson = userRegisterService.legalRegister(legalPersonRegisterDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/users/{username}")
                .buildAndExpand(legalPerson.username())
                .toUri();
        return ResponseEntity.created(location).body(legalPerson);
    }

}
