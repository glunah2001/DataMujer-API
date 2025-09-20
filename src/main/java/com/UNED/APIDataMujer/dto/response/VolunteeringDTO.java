package com.UNED.APIDataMujer.dto.response;

import java.time.LocalDate;

public record VolunteeringDTO(
    long id,
    String username,
    long activityId,
    String activity,
    String description,
    String location,
    boolean isOnSite,
    LocalDate volunteerDate,
    String activityRole
) { }
