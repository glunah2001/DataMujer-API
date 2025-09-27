package com.UNED.APIDataMujer.controller;

import com.UNED.APIDataMujer.dto.request.ActivityRegisterDTO;
import com.UNED.APIDataMujer.service.resource.ActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * RestController con el propósito de recibir peticiones relacionadas con las
 * actividades.
 * @author glunah2001
 * @see ActivityService
 * */
@RestController
@RequiredArgsConstructor
@RequestMapping("/activity")
public class ActivityController {

    private final ActivityService activityService;

    /**
     * Función encargada de insertar una nueva actividad en la BD.
     * Este endpoint está limitado a los usuarios con roles de mentor o administrador.
     * @param dto información de la actividad a insertar.
     * @return actividad recién insertada en la bd.
     * */
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_MENTOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> createNewActivity(@Valid @RequestBody final ActivityRegisterDTO dto){
        var activity = activityService.createNewActivity(dto);
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/activity/{id}")
                .buildAndExpand(activity.id())
                .toUri();

        return ResponseEntity.created(location).body(activity);
    }

    /**
     * Función encargada de obtener una sola actividad según su id.
     * @param id id de la actividad.
     * @return actividad concreta recuperada de la bd.
     * */
    @GetMapping("/{id}")
    public ResponseEntity<?> getActivity(@PathVariable long id){
        var activity = activityService.getActivityDto(id);
        return ResponseEntity.ok(activity);
    }

    /**
     * Función encargada de obtener todas las actividades que no han
     * sido finalizadas.
     * @param page indicador de paginación.
     * @return página de 25 actividades en DTO.
     * */
    @GetMapping
    public ResponseEntity<?> getAllActiveActivities(@RequestParam(defaultValue = "0") int page){
        var activities = activityService.getAllActiveActivities(page);
        return ResponseEntity.ok(activities);
    }

    /**
     * Función encargada de dar de baja una actividad.
     * @param id identificador de la actividad.
     * @param auth credenciales del usuario mentor/administrador.
     * @return se espera un código 204 (Éxito pero sin contenido que retornar)
     * */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_MENTOR', 'ROLE_ADMIN')")
    public ResponseEntity<?> deleteActivity(@PathVariable long id,
                                            final Authentication auth){
        activityService.deleteActivity(id, auth);
        return ResponseEntity.noContent().build();
    }

}
