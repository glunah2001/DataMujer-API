package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.SimplePage;
import com.UNED.APIDataMujer.dto.request.ActivityRegisterDTO;
import com.UNED.APIDataMujer.dto.response.ActivityDTO;
import com.UNED.APIDataMujer.entity.Activity;

public interface ActivityService {
    ActivityDTO createNewActivity(ActivityRegisterDTO dto);
    ActivityDTO getActivityDto(long id);
    Activity getActivity(long id);
    SimplePage<ActivityDTO> getAllActiveActivities(int page);
}
