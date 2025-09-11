package com.UNED.APIDataMujer.service.registration;

import com.UNED.APIDataMujer.entity.User;

public interface ActivationService {
    void activateAccount(String token);
    void generateActivationToken(User user);
}
