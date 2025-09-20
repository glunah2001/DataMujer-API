package com.UNED.APIDataMujer.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record VolunteeringRegisterDTO(
        @NotNull(message = "EL dato \"Actividad\" es obligatorio")
        Long activityId,
        @NotBlank(message = "EL dato \"Nombre de Usuario\" es obligatorio")
        String username,
        @NotNull(message = "EL dato \"Fecha de Voluntariado\" es obligatorio")
        @Future(message = "El voluntariado debe notificarse con un margen de tiempo de 1 día mínimo")
        LocalDate volunteeringDate,
        @NotNull(message = "EL dato \"Rol en Actividad\" es obligatorio")
        @Size(max = 15, message = "Describa el rol en 15 o menos caracteres")
        String activityRole
) { }
