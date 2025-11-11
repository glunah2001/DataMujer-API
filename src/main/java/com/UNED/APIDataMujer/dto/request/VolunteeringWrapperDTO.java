package com.UNED.APIDataMujer.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record VolunteeringWrapperDTO(
        @NotNull(message = "El dato \"Actividad\" es obligatorio")
        Long activityId,
        @NotNull(message = "Usted está presentando un listado de voluntariados nulos")
        @NotEmpty(message = "Usted está presentando un listado de voluntariados vacío")
        List<@Valid VolunteeringRegisterDTO> volunteering
) { }
