package com.UNED.APIDataMujer.controller;

import com.UNED.APIDataMujer.service.resource.VolunteeringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController encargado de manejar las solicitudes relacionadas con los voluntariados
 * cada endpoint está restringido para usuario con rol de mentor o administrador
 * @author glunah2001
 * @see VolunteeringService
 * */
@RestController
@RequiredArgsConstructor
@RequestMapping("/volunteering")
public class VolunteeringController {

    private final VolunteeringService volunteeringService;

    /**
     * Función encargada de atender las solicitudes que buscan obtener todos
     * los voluntariados de una persona en actividades sin finalizar (pendientes)
     * @param auth credenciales de la person
     * @return listado de sus voluntariados
     * */
    @GetMapping("/me/pending")
    @PreAuthorize("hasAnyAuthority('ROLE_MENTOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> getMyPending(Authentication auth){
        var volunteering = volunteeringService.getMyPendingVolunteering(auth);
        return ResponseEntity.ok(volunteering);
    }

    /**
     * Función encargada de atender las solicitudes que buscan obtener todos
     * los voluntariados en una actividad en concreto
     * @param id id de la actividad
     * @return listado de los voluntariados de dicha actividad
     * */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MENTOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> getVolunteeringForAnActivity(@PathVariable long id){
        var volunteering = volunteeringService.getVolunteeringForAnActivity(id);
        return ResponseEntity.ok(volunteering);
    }

}
