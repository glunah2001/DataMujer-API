package com.UNED.APIDataMujer.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record ActivityRegisterDTO(
        @NotNull(message = "El dato \"Nombre de la actividad\" es obligatorio")
        @Size(max = 50, message = "El nombre de la actividad no debe " +
                "superar los 50 caracteres")
        String activity,
        @NotNull(message = "El dato \"Descripción de la actividad\" es obligatorio")
        @Size(max = 255, message = "La descripción de la actividad no debe " +
                "superar los 255 caracteres")
        String description,
        @NotNull(message = "El dato \"Ubicación/Plataforma\" es obligatorio")
        @Size(max = 255, message = "La ubicación/plataforma de la actividad no debe " +
                "superar los 255 caracteres")
        String location,
        @NotNull(message = "El dato \"Modalidad de la actividad\" es obligatorio")
        Boolean isOnSite,
        @NotNull(message = "EL dato \"Fecha de inicio\" es obligatorio")
        @Future(message = "La fecha de apertura debe ser superior a la actual y la " +
                "actividad debe notificarse con un margen de tiempo de 1 día mínimo.")
        LocalDateTime startDate,
        @NotNull(message = "EL dato \"Fecha de cierre\" es obligatorio")
        @Future(message = "La fecha de cierre debe ser superior a la actual y a la de " +
                "apertura (con al menos una hora de duración)")
        LocalDateTime endDate
) { }
