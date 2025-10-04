package com.UNED.APIDataMujer.dto.request;

import com.UNED.APIDataMujer.enums.Classification;
import com.UNED.APIDataMujer.enums.Method;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record PaymentRegisterDTO(
        @NotBlank(message = "El dato \"descripción\" es obligatorio")
        String description,
        @NotNull(message = "El dato \"clasificación\" es obligatorio")
        Classification classification,
        @NotNull(message = "El dato \"método de pago\" es obligatorio")
        Method method,
        @PastOrPresent(message = "El dato \"Fecha de pago\" debe presentar una fecha válida actual o pasada que " +
                "pueda contrastarse.")
        LocalDateTime paymentDate,
        @NotNull(message = "El dato \"Está pagado\" es obligatorio")
        Boolean isPaid,
        @NotNull(message = "El dato \"Monto\" es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El monto total debe ser mayor que 0")
        BigDecimal totalAmount
) { }
