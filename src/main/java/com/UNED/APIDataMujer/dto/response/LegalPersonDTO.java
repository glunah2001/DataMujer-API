package com.UNED.APIDataMujer.dto.response;

import com.UNED.APIDataMujer.enums.Country;

import java.time.LocalDate;

public record LegalPersonDTO(
        String legalId,
        String businessName,
        LocalDate foundationDate,
        String phoneNumber,
        Country country,
        String location,
        String username,
        String email
) { }
