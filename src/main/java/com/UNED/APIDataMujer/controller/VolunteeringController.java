package com.UNED.APIDataMujer.controller;

import com.UNED.APIDataMujer.dto.request.VolunteeringRegisterDTO;
import com.UNED.APIDataMujer.dto.request.VolunteeringWrapperDTO;
import com.UNED.APIDataMujer.service.resource.VolunteeringService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * RestController encargado de manejar las solicitudes relacionadas con los voluntariados
 * cada endpoint está restringido para usuario con rol de mentor o administrador.
 * @author glunah2001
 * @see VolunteeringService
 * */
@RestController
@RequiredArgsConstructor
@RequestMapping("/volunteering")
public class VolunteeringController {

    private final VolunteeringService volunteeringService;

    /**
     * Función encargada de obtener un voluntariado a través de su id.
     * @param id id único del voluntariado a buscar.
     * @return Dto. Del voluntariado buscado.
     * */
    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MENTOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> getVolunteering(@RequestParam long id){
        var volunteering = volunteeringService.getVolunteering(id);
        return ResponseEntity.ok(volunteering);
    }

    /**
     * Función encargada de atender las solicitudes que buscan obtener todos
     * los voluntariados de una persona en actividades sin finalizar (pendientes).
     * @param auth credenciales de la person.
     * @param page indicador de paginación.
     * @return listado de sus voluntariados.
     * */
    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('ROLE_MENTOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> getMyPending(Authentication auth,
                                          @RequestParam(defaultValue = "0") int page){
        var volunteering = volunteeringService.getMyPendingVolunteering(auth, page);
        return ResponseEntity.ok(volunteering);
    }

    /**
     * Función encargada de atender las solicitudes que buscan obtener todos
     * los voluntariados en una actividad en concreto.
     * @param activityId activityId de la actividad.
     * @param page indicador de paginación.
     * @return listado de los voluntariados de dicha actividad.
     * */
    @GetMapping("volunteersInActivity")
    @PreAuthorize("hasAnyAuthority('ROLE_MENTOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> getVolunteeringForAnActivity(@RequestParam long activityId,
                                                          @RequestParam(defaultValue = "0") int page){
        var volunteering = volunteeringService.getVolunteeringForAnActivity(activityId, page);
        return ResponseEntity.ok(volunteering);
    }

    /**
     * Función encargada de insertar un lote de voluntariados.
     * @param dto varios Dto. De voluntariado.
     * @return Código 201 de la creación.
     * */
    @PostMapping("/multiple")
    @PreAuthorize("hasAnyAuthority('ROLE_MENTOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> postMultipleVolunteering(@Valid @RequestBody VolunteeringWrapperDTO dto){
        var activityId = volunteeringService.createVolunteering(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/volunteering/volunteersInActivity")
                .queryParam("activityId", activityId)
                .build()
                .toUri();

        return ResponseEntity.created(location).build();
    }

    /**
     * Función encargada de insertar el voluntariado propio de un usuario mentor/administrador.
     * @param dto información de un voluntariado propio.
     * @return código 201 de la creación.
     * */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MENTOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> postVolunteering(@Valid @RequestBody VolunteeringRegisterDTO dto){
        var volunteering = volunteeringService.createVolunteering(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/volunteering")
                .queryParam("id", volunteering.id())
                .build()
                .toUri();
        return ResponseEntity.created(location).body(volunteering);
    }

}
