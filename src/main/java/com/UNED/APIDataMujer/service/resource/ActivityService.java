package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.request.ActivityRegisterDTO;
import com.UNED.APIDataMujer.dto.response.ActivityDTO;
import org.springframework.security.core.Authentication;

public interface ActivityService {
    ActivityDTO createNewActivity(ActivityRegisterDTO dto,
                                  Authentication auth);
    ActivityDTO getActivity(long id);
}
