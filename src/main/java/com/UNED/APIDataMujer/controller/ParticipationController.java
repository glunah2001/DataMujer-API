package com.UNED.APIDataMujer.controller;

import com.UNED.APIDataMujer.service.resource.ParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * Endpoints para las participaciones.
 * @author glunah2001
 * @see ParticipationService
 * */
@RestController
@RequiredArgsConstructor
@RequestMapping("/participation")
public class ParticipationController {

    private final ParticipationService participationService;

    /**
     * Endpoint para recuperar una participación.
     * @param id identificador de la participación.
     * @return información de la participación en DTO.
     * */
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<?> getParticipation(@RequestParam(defaultValue = "0") long id){
        var participation = participationService.getParticipation(id);
        return ResponseEntity.ok(participation);
    }

    /**
     * Endpoint para consultar todas las participaciones de una actividad sin importar su estado.
     * @param auth credenciales.
     * @param id identificador de la actividad.
     * @param page pagina.
     * */
    @GetMapping("/activity")
    @PreAuthorize("hasAnyAuthority('ROLE_MENTOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> getActivityParticipation(final Authentication auth,
                                                      @RequestParam(defaultValue = "0") long id,
                                                      @RequestParam(defaultValue = "0") int page){
        var participation = participationService.getActivityParticipation(auth, id, page);
        return ResponseEntity.ok(participation);
    }

    /**
     * endpoint para solicitar mis participaciones.
     * @param auth credenciales de usuario.
     * @param page paginación.
     * @return paginado con los endpoints de las participaciones en actividades sin clausurar
     * */
    @GetMapping("/me")
    public ResponseEntity<?> getMyParticipation(final Authentication auth,
                                                @RequestParam(defaultValue = "0") int page){
        var participation = participationService.getMyParticipations(auth, page);
        return ResponseEntity.ok(participation);
    }

    /**
     * Endpoint para crear una participación en una actividad.
     * @param auth credenciales.
     * @param activityId identificador de la actividad.
     * @return código 201 y el contenido de la participación junto a su ruta de consulta.
     * */
    @PostMapping
    public ResponseEntity<?> createParticipation(final Authentication auth,
                                                 @RequestParam(defaultValue = "0") long activityId){
        var participation = participationService.createParticipation(auth, activityId);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/participation")
                .queryParam("id", participation.id())
                .build()
                .toUri();
        return ResponseEntity.created(location).body(participation);
    }

    /**
     * Endpoint para indicar al sistema que la participación se ha puesto en marcha.
     * @param auth credenciales.
     * @param id identificador de la participación.
     * @return contenido actualizado de la participación.
     * */
    @PutMapping("/start")
    public ResponseEntity<?> startParticipation(final Authentication auth,
                                                @RequestParam(defaultValue = "0") long id){
        var participation = participationService.updateStartDate(auth, id);
        return ResponseEntity.ok(participation);
    }

    /**
     * Endpoint para indicar al sistema que la participación se ha cancelado. (cierre antes de tiempo)
     * @param auth credenciales.
     * @param id identificador de la participación.
     * @return contenido actualizado de la participación.
     * */
    @PutMapping("/cancel")
    public ResponseEntity<?> cancelParticipation(final Authentication auth,
                                                 @RequestParam(defaultValue = "0") long id){
        var participation = participationService.cancelParticipation(auth, id);
        return ResponseEntity.ok(participation);
    }

    /**
     * Endpoint para eliminar una participación.
     * @param auth credenciales.
     * @param id identificador de la participación.
     * @return código 204.
     * */
    @DeleteMapping
    public ResponseEntity<?> deleteParticipation(final Authentication auth,
                                                 @RequestParam(defaultValue = "0") long id){
        participationService.deleteParticipation(auth, id);
        return ResponseEntity.noContent().build();
    }
}