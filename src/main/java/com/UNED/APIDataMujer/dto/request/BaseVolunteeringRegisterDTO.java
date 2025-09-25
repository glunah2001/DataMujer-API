package com.UNED.APIDataMujer.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record BaseVolunteeringRegisterDTO(
        @NotNull(message = "EL dato \"Actividad\" es obligatorio")
        Long activityId,
        @NotNull(message = "El dato \"Inicio de Turno\" es obligatorio")
        @Future(message = "El voluntariado debe notificarse con un margen de tiempo de 1 día mínimo")
        LocalDateTime startShift,
        @NotNull(message = "EL dato \"Fin de Turno\" es obligatorio")
        @Future(message = "El voluntariado debe notificarse con un margen de tiempo de 1 día mínimo")
        LocalDateTime endShift,
        @NotNull(message = "EL dato \"Rol en Actividad\" es obligatorio")
        @Size(max = 15, message = "Describa el rol en 15 o menos caracteres")
        String activityRole
) { }
