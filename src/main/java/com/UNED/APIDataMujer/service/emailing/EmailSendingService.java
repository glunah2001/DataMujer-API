package com.UNED.APIDataMujer.service.emailing;

public interface EmailSendingService {
    void sendEmail(String to, String subject, String body);
}
