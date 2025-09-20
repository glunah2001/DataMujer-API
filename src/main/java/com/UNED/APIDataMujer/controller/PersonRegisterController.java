package com.UNED.APIDataMujer.controller;

import com.UNED.APIDataMujer.dto.request.LegalPersonRegisterDTO;
import com.UNED.APIDataMujer.dto.request.PhysicalPersonRegisterDTO;
import com.UNED.APIDataMujer.service.registration.PersonRegisterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * RestController para registro de personas como usuarios.
 * @author glunah2001
 * @see PersonRegisterService
 * */
@RestController
@RequiredArgsConstructor
@RequestMapping("/register")
public class PersonRegisterController {

    private final PersonRegisterService personRegisterService;


    /**
     * Función encargada del registro de personas físicas como usuarios dentro del sistema
     * @param physicalPersonRegisterDTO contiene toda la información de la persona
     *                                  física para su almacenamiento
     * @return un estado 201 CREATED avisando de la necesidad de activar su cuenta con un correo enviado.
     * */
    @PostMapping("/physical")
    public ResponseEntity<?> register(@Valid @RequestBody final PhysicalPersonRegisterDTO physicalPersonRegisterDTO){
        final var physicalPerson = personRegisterService.physicalRegister(physicalPersonRegisterDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/users/{username}")
                .buildAndExpand(physicalPerson.username())
                .toUri();
        return ResponseEntity.created(location).body("Su cuenta ha sido creada. Confirme la activación de su cuenta " +
                "mediante el correo enviado a la dirección registrada.");
    }

    /**
     * Función encargada del registro de personas jurídicas como usuarios dentro del sistema
     * @param legalPersonRegisterDTO contiene toda la información de
     *                               la persona legal para su almacenamiento
     * @return un estado 201 CREATED avisando de la necesidad de activar su cuenta con un correo enviado.
     * */
    @PostMapping("/legal")
    public ResponseEntity<?> register(@Valid @RequestBody final LegalPersonRegisterDTO legalPersonRegisterDTO){
        final var legalPerson = personRegisterService.legalRegister(legalPersonRegisterDTO);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/users/{username}")
                .buildAndExpand(legalPerson.username())
                .toUri();
        return ResponseEntity.created(location).body("Su cuenta ha sido creada. Confirme la activación de su cuenta " +
                "mediante el correo enviado a la dirección registrada.");
    }

}
