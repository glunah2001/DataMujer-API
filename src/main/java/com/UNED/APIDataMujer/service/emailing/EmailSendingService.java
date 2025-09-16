package com.UNED.APIDataMujer.service.emailing;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Clase encargada del envío de correos a los usuarios indicados mediante
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
        message.setFrom(from);

        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    /**
     * Función asíncrona encargada de enviar correos con plantilla HTML
     * @param to el correo del destinatario.
     * @param subject el asunto del correo.
     * @param body cuerpo del correo.
     * @param username nombre de usuario de la persona destinataria.
     * */
    @Async
    public void sendEmail(String to, String subject, String body, String username){
        MimeMessage message = mailSender.createMimeMessage();

        try {
            message.setFrom(new InternetAddress(from));
            message.setRecipients(MimeMessage.RecipientType.TO, to);
            message.setSubject(subject);

            String safeUser = HtmlUtils.htmlEscape(username);

            String htmlTemplate = loadTemplate("EmailTemplate.html");
            String htmlContent = htmlTemplate.replace("${Nombre}", safeUser)
                    .replace("${Mensaje}", body);

            message.setContent(htmlContent, "text/html; charset=utf-8");

            mailSender.send(message);
        } catch (MessagingException ex) {
            System.out.println("Error en el envío de correo");
        } catch (IOException ex){
            System.out.println("Error en la lectura de plantilla");
        }
    }

    /**
     * Función auxiliar encargada de buscar y permitir la carga de plantilla
     * HTML desde classpath como String al leerse.
     * @param filename nombre de la plantilla HTML que se debe cargar desde
     *                 classpath de resources.
     * @return plantilla HTML cargada como String.
     * */
    private String loadTemplate(String filename) throws IOException {
        ClassPathResource resource = new ClassPathResource("templates/" + filename);
        try (InputStream inputStream = resource.getInputStream()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

}
