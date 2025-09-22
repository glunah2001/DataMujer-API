package com.UNED.APIDataMujer.dto.response;

import java.time.LocalDateTime;

public record VolunteeringDTO(
    long id,
    String username,
    long activityId,
    String activity,
    String description,
    String location,
    boolean isOnSite,
    LocalDateTime startShift,
    LocalDateTime endShift,
    String activityRole
) { }
