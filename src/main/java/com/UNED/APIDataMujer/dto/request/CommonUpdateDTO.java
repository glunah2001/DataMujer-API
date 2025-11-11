package com.UNED.APIDataMujer.dto.request;

import com.UNED.APIDataMujer.enums.Country;
import jakarta.validation.constraints.*;

public record CommonUpdateDTO(
        @NotBlank(message = "El Dato \"Email\" es obligatorio.")
        @Email(message = "El email digitado no es válido.")
        String email,

        @NotBlank(message = "El dato \"Número de Teléfono\" es obligatorio.")
        @Size(max = 17, message = "El número de teléfono no puede superar los 17 caracteres en total.")
        @Pattern(
                regexp = "^\\+[1-9]\\d{0,2}\\s\\d{1,14}$",
                message = "El número de teléfono debe: empezar con \"+\", tener un prefijo de 1 a 3 carácteres " +
                        "seguido de un espacio, luego el número telefónico sin separaciones. Ej. \"+X NNNNNNNNNNNNNN\"."
        )
        String phoneNumber,

        @NotNull(message = "El dato \"País\" es obligatorio.")
        Country country,

        @NotBlank(message = "El dato \"Ubicación\" es obligatorio.")
        String location
) { }
