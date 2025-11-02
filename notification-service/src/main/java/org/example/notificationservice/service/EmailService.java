package org.example.notificationservice.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("sorohov344@gmail.com");

            mailSender.send(message);
            System.out.println("Email отправлен на: " + to);
        } catch (Exception e) {
            System.err.println("Ошибка отправки email: " + e.getMessage());
            throw new RuntimeException("Ошибка отправки email", e);
        }
    }
}