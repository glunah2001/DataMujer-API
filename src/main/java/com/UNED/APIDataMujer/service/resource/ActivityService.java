package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.request.ActivityRegisterDTO;
import com.UNED.APIDataMujer.dto.response.ActivityDTO;
import com.UNED.APIDataMujer.entity.Activity;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ActivityService {
    ActivityDTO createNewActivity(ActivityRegisterDTO dto);
    ActivityDTO getActivityDto(long id);
    Activity getActivity(long id);
    List<ActivityDTO> getAllActiveActivities();
}
