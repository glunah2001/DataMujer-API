package com.UNED.APIDataMujer.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AffiliatesPaymentReportDTO(
        String username,
        LocalDateTime lastPaymentDate,
        LocalDateTime affiliateExpirationDate,
        BigDecimal totalPaid,
        boolean isAffiliate
) { }
