package com.UNED.APIDataMujer.dto.request;

import com.UNED.APIDataMujer.enums.Country;
import jakarta.validation.constraints.*;

public record CommonRegisterDTO(
        @NotBlank(message = "El Dato \"Nombre de Usuario\" es obligatorio.")
        @Size(min = 3, max = 15, message = "El nombre de usuario debe contener 3 a 15 caracteres.")
        String username,

        @NotBlank(message = "El Dato \"Email\" es obligatorio.")
        @Email(message = "El email digitado no es válido.")
        String email,

        @NotBlank(message = "El Dato \"Contraseña\" es obligatorio.")
        @Size(min = 12, max = 25, message = "Una contraseña segura consta de 12 a 25 caracteres")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_])(?=\\S+$).{12,25}$",
                message = "La contraseña debe contener: al menos una mayúscula, una minúscula, " +
                        "un número y un carácter especial (@#$%^&+=_) sin espacios.")
        String password,

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
) {}
