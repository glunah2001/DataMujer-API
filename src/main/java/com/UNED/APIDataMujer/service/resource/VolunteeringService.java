package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.request.VolunteeringRegisterDTO;
import com.UNED.APIDataMujer.dto.response.VolunteeringDTO;
import com.UNED.APIDataMujer.entity.Activity;
import com.UNED.APIDataMujer.entity.Volunteering;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface VolunteeringService {
    VolunteeringDTO getVolunteering(long id);
    List<VolunteeringDTO> getMyPendingVolunteering(Authentication auth);
    List<VolunteeringDTO> getVolunteeringForAnActivity(long id);
    long insertVolunteering(List<VolunteeringRegisterDTO> list);
    VolunteeringDTO insertVolunteering(VolunteeringRegisterDTO dto);
}
