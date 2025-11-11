package com.UNED.APIDataMujer.service.resource;

import com.UNED.APIDataMujer.dto.request.CommonUpdateDTO;
import com.UNED.APIDataMujer.dto.response.ProfileDTO;
import com.UNED.APIDataMujer.entity.User;
import org.springframework.security.core.Authentication;


public interface UserService {
    User getMyUser(Authentication authentication);
    User updateUserData(Authentication auth, CommonUpdateDTO dto);
    User getUserByUsername(String username);
    ProfileDTO getMyProfile(Authentication authentication);
    ProfileDTO getPersonByUsername(String username);
    String setRole(Authentication auth, String username, int role);
    String setAffiliate(String username);
}
