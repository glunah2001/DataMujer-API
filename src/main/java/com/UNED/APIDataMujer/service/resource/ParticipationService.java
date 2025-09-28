package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.SimplePage;
import com.UNED.APIDataMujer.dto.response.ParticipationDTO;
import org.springframework.security.core.Authentication;

public interface ParticipationService {
    ParticipationDTO getParticipation(long participationId);
    SimplePage<ParticipationDTO> getMyParticipations(Authentication authentication, int page);
    ParticipationDTO createParticipation(Authentication auth, long activityId);
    ParticipationDTO updateStartDate(Authentication auth, long participationId);
    ParticipationDTO cancelParticipation(Authentication auth, long participationId);
    void deleteParticipation(Authentication auth, long participationId);
}
