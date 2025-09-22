package com.UNED.APIDataMujer.controller;

import com.UNED.APIDataMujer.dto.request.LegalPersonUpdateDTO;
import com.UNED.APIDataMujer.dto.request.PhysicalPersonUpdateDTO;
import com.UNED.APIDataMujer.service.resource.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * RestController (con ruta protegida) encargado de todas las consultas relacionadas
 * con los usuarios
 * @author glunah2001
 * @see UserService
 * */
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    /**
     * Función encargada de obtener el perfil propio de la persona
     * @param auth Authentication obtenida de los filtros de seguridad con la cual saber
     *             cuál usuario está solicitando su información personal.
     * @return un dto. Con toda la información pública no comprometida del usuario.
     * */
    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(final Authentication auth){
        final var dto = userService.finMyProfile(auth);
        return ResponseEntity.ok(dto);
    }

    /**
     * Función encargada de actualizar el perfil de una persona física con nueva
     * información pública no comprometida.
     * @param auth Authentication obtenida de los filtros de seguridad con la cual saber
     *             cuál usuario está actualizando su información personal.
     * @param updateDto Dto. Con toda la información a actualizar.
     * @return Dto. Con toda su información actualizada.
     * */
    @PutMapping("/me/physical")
    public ResponseEntity<?> updateMyPhysicalProfile(final Authentication auth,
                                             @Valid @RequestBody PhysicalPersonUpdateDTO updateDto){
        final var dto = userService.updateMyPhysicalProfile(auth, updateDto);
        return ResponseEntity.ok(dto);
    }

    /**
     * Función encargada de actualizar el perfil de una persona legal con nueva
     * información pública no comprometida.
     * @param auth Authentication obtenida de los filtros de seguridad con la cual saber
     *             cuál usuario está actualizando su información personal.
     * @param updateDto Dto. Con toda la información a actualizar.
     * @return Dto. Con toda su información actualizada.
     * */
    @PutMapping("me/legal")
    public ResponseEntity<?> updateMyLegalProfile(final Authentication auth,
                                             @Valid @RequestBody LegalPersonUpdateDTO updateDto){
        final var dto = userService.updateMyLegalProfile(auth, updateDto);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/search/username")
    public ResponseEntity<?> findByUsername(@RequestParam String username) {
        return ResponseEntity.ok(userService.findPersonByUsername(username));
    }

    @GetMapping("/search/national-id")
    public ResponseEntity<?> findByNationalId(@RequestParam String id) {
        return ResponseEntity.ok(userService.findPersonByNationalId(id));
    }

    @GetMapping("/search/legal-id")
    public ResponseEntity<?> findByLegalId(@RequestParam String id) {
        return ResponseEntity.ok(userService.findPersonByLegalId(id));
    }

    @GetMapping("/search/name")
    public ResponseEntity<?> findByName(@RequestParam String name) {
        return ResponseEntity.ok(userService.findPersonByName(name));
    }

    @GetMapping("/search/surname")
    public ResponseEntity<?> findBySurname(@RequestParam String surname) {
        return ResponseEntity.ok(userService.findPersonBySurname(surname));
    }

    @GetMapping("/search/business")
    public ResponseEntity<?> findByBusinessName(@RequestParam String businessName) {
        return ResponseEntity.ok(userService.findPersonByBusinessName(businessName));
    }
}
