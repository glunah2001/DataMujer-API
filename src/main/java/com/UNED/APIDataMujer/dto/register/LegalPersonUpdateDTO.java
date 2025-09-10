package com.UNED.APIDataMujer.dto.register;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record LegalPersonUpdateDTO(
        @Valid
        @NotNull
        CommonUpdateDTO commonUpdateDTO,

        @NotBlank(message = "El Dato \"Nombre de Entidad\" es obligatorio.")
        @Size(max = 50, message = "El dato no puede ser mayor a 50 caracteres")
        String businessName,

        @NotNull(message = "La fecha de fundación no puede estar vacía.")
        LocalDate foundationDate
) { }
