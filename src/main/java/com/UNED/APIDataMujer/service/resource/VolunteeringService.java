package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.SimplePage;
import com.UNED.APIDataMujer.dto.request.BaseVolunteeringRegisterDTO;
import com.UNED.APIDataMujer.dto.request.VolunteeringRegisterDTO;
import com.UNED.APIDataMujer.dto.request.VolunteeringWrapperDTO;
import com.UNED.APIDataMujer.dto.response.VolunteeringDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface VolunteeringService {
    VolunteeringDTO getVolunteering(long id);
    SimplePage<VolunteeringDTO> getMyPendingVolunteering(Authentication auth, int page);
    SimplePage<VolunteeringDTO> getVolunteeringForAnActivity(long id, int page);
    long insertVolunteering(VolunteeringWrapperDTO dto);
    VolunteeringDTO insertMyVolunteering(Authentication auth, BaseVolunteeringRegisterDTO dto);
}
