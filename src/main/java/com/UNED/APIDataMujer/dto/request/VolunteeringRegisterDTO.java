package com.UNED.APIDataMujer.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record VolunteeringRegisterDTO(
        @Valid
        BaseVolunteeringRegisterDTO volunteeringData,
        @NotBlank(message = "EL dato \"Nombre de Usuario\" es obligatorio")
        String username
) { }
