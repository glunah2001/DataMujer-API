package com.UNED.APIDataMujer.dto.register;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record LegalPersonRegisterDTO(
        @Valid
        @NotNull
        CommonRegisterDTO commonRegisterDTO,

        @NotBlank(message = "El Dato \"Identificación Legal\" es obligatorio.")
        @Size(min = 10, max = 10, message = "Por favor, digite la cédula jurídica de su entidad. " +
                "10 caracteres mínimo.")
        String legalId,

        @NotBlank(message = "El Dato \"Nombre de Entidad\" es obligatorio.")
        @Size(max = 50, message = "El dato no puede ser mayor a 50 caracteres")
        String businessName,

        @NotNull(message = "La fecha de fundación no puede estar vacía.")
        LocalDate foundationDate
) { }
