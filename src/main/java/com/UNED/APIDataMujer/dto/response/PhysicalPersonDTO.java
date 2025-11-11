package com.UNED.APIDataMujer.dto.response;

import com.UNED.APIDataMujer.enums.Country;

import java.time.LocalDate;

public record PhysicalPersonDTO(
        String nationalId,
        String firstSurname,
        String secondSurname,
        String name,
        String profession,
        LocalDate birthDate,
        String phoneNumber,
        Country country,
        String location,
        String username,
        String email
) implements ProfileDTO { }
