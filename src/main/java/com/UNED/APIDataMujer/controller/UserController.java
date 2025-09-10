package com.UNED.APIDataMujer.controller;

import com.UNED.APIDataMujer.dto.register.LegalPersonUpdateDTO;
import com.UNED.APIDataMujer.dto.register.PhysicalPersonUpdateDTO;
import com.UNED.APIDataMujer.service.resource.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<?> getMyProfile(final Authentication auth){
        final var dto = userService.getMyProfile(auth);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/me/physical")
    public ResponseEntity<?> updateMyProfile(final Authentication auth,
                                             @Valid @RequestBody PhysicalPersonUpdateDTO updateDto){
        final var dto = userService.updateMyPhysicalProfile(auth, updateDto);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("me/legal")
    public ResponseEntity<?> updateMyProfile(final Authentication auth,
                                             @Valid @RequestBody LegalPersonUpdateDTO updateDto){
        final var dto = userService.updateMyLegalProfile(auth, updateDto);
        return ResponseEntity.ok(dto);
    }
}
