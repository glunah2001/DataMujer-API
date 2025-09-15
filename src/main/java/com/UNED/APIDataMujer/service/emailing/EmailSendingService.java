package com.UNED.APIDataMujer.service.emailing;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * Clase encargada del envio de correos a los usuarios indicados mediante
 * su correo desde un correo autorizado por el patrocinador/propietario.
 * @author glunah2001
 * */
@Service
@RequiredArgsConstructor
public class EmailSendingService {

    @Value("${spring.mail.username}")
    private String from;
    private final JavaMailSender mailSender;

    /**
     * Función asíncrona encargada de enviar correos planos. Es decir,
     * sin plantilla html
     * @param to el correo del destinatario
     * @param subject el asunto del correo
     * @param body cuerpo del correo
     * */
    @Async
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(to);
        message.setFrom("glunah2001@gmail.com");

        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }
}
