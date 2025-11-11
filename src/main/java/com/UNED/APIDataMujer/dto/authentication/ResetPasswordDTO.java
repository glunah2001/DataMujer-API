package com.UNED.APIDataMujer.dto.authentication;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ResetPasswordDTO(
        @NotBlank(message = "El Dato \"Token\" es obligatorio.")
        String token,

        @NotBlank(message = "El Dato \"Contraseña\" es obligatorio.")
        @Size(min = 12, max = 25, message = "Una contraseña segura consta de 12 a 25 caracteres")
        @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=_])(?=\\S+$).{12,25}$",
                message = "La contraseña debe contener: al menos una mayúscula, una minúscula, " +
                        "un número y un carácter especial (@#$%^&+=_) sin espacios.")
        String password
) { }
