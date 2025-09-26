package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.SimplePage;
import com.UNED.APIDataMujer.dto.request.ActivityRegisterDTO;
import com.UNED.APIDataMujer.dto.response.ActivityDTO;
import com.UNED.APIDataMujer.entity.Activity;
import org.springframework.security.core.Authentication;

public interface ActivityService {
    ActivityDTO createNewActivity(ActivityRegisterDTO dto);
    ActivityDTO getActivityDto(long id);
    SimplePage<ActivityDTO> getAllActiveActivities(int page);
    void deleteActivity(long id, Authentication auth);
}
