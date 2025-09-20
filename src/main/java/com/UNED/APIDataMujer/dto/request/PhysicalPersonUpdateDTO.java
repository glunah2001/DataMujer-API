package com.UNED.APIDataMujer.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record PhysicalPersonUpdateDTO(
        @Valid
        @NotNull
        CommonUpdateDTO commonUpdateDTO,

        @NotBlank(message = "El Dato \"Primer Apellido\" es obligatorio.")
        @Size(max = 15, message = "El dato no puede ser mayor a 15 caracteres")
        String firstSurname,

        @NotBlank(message = "El Dato \"Segundo Apellido\" es obligatorio.")
        @Size(max = 15, message = "El dato no puede ser mayor a 15 caracteres")
        String secondSurname,

        @NotBlank(message = "El Dato \"Nombre\" es obligatorio.")
        @Size(max = 15, message = "El dato no puede ser mayor a 15 caracteres")
        String name,

        @NotBlank(message = "El Dato \"Profesión\" es obligatorio.")
        String profession,

        @NotNull(message = "La fecha de nacimiento no puede estar vacía.")
        LocalDate birthDate
) { }
