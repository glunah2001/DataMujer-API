package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.SimplePage;
import com.UNED.APIDataMujer.dto.request.BaseVolunteeringRegisterDTO;
import com.UNED.APIDataMujer.dto.request.VolunteeringUpdateDTO;
import com.UNED.APIDataMujer.dto.request.VolunteeringWrapperDTO;
import com.UNED.APIDataMujer.dto.response.VolunteeringDTO;
import com.UNED.APIDataMujer.entity.Activity;
import com.UNED.APIDataMujer.entity.User;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;

public interface VolunteeringService {
    VolunteeringDTO getVolunteering(long id);
    SimplePage<VolunteeringDTO> getMyPendingVolunteering(Authentication auth, int page);
    SimplePage<VolunteeringDTO> getVolunteeringForAnActivity(long id, int page);
    void createOrganizerVolunteering(String username,
                                     Activity activity,
                                     LocalDateTime startDate,
                                     LocalDateTime endDate);
    long createVolunteering(VolunteeringWrapperDTO dto);
    VolunteeringDTO createMyVolunteering(Authentication auth, BaseVolunteeringRegisterDTO dto);
    boolean isUserOrganizer(long activityId, long userId);
    VolunteeringDTO updateVolunteering(long volunteeringId, VolunteeringUpdateDTO dto);
    void deleteVolunteering(long volunteeringId, Authentication auth);
}
