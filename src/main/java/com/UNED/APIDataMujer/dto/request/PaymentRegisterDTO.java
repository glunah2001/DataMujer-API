package com.UNED.APIDataMujer.dto.request;

import com.UNED.APIDataMujer.enums.Classification;
import com.UNED.APIDataMujer.enums.Method;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PaymentRegisterDTO(
        @NotBlank(message = "La descripción no puede ser nula ni vacía")
        String description,
        @NotNull(message = "La clasificación no puede ser nula")
        Classification classification,
        @NotNull(message = "El método de pago no puede ser nulo")
        Method method,
        @NotBlank(message = "El campo monthYearPayment no puede ser nulo ni vacío")
        @Pattern(regexp = "^(\\d{4})-(0[1-9]|1[0-2])$",
                message = "monthYearPayment debe tener el formato YYYY-MM"
        )
        String monthYearPayment,
        LocalDateTime paymentDate,
        @NotNull(message = "El campo isPaid no puede ser nulo")
        Boolean isPaid,
        @NotNull(message = "El monto total no puede ser nulo")
        @DecimalMin(value = "0.0", inclusive = false, message = "El monto total debe ser mayor que 0")
        BigDecimal totalAmount
) { }
