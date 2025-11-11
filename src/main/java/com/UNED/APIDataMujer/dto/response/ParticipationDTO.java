package com.UNED.APIDataMujer.dto.response;

import com.UNED.APIDataMujer.enums.ParticipationState;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ParticipationDTO(
        long id,
        LocalDate registerDate,
        LocalDate startDate,
        LocalDate endDate,
        String username,
        long activityId,
        String activity,
        String description,
        String location,
        boolean isOnSite,
        LocalDateTime startShift,
        LocalDateTime endShift,
        ParticipationState participationState
) { }