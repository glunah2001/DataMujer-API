package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.response.ParticipationDTO;
import org.springframework.security.core.Authentication;

public interface ParticipationService {
    ParticipationDTO createParticipation(Authentication auth);
    ParticipationDTO updateStartDate();
    ParticipationDTO cancelParticipation(Authentication auth);
    void deleteParticipation(Authentication auth);
}
