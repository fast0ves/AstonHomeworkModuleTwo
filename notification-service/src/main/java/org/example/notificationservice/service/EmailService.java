package org.example.notificationservice.service;

import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final CircuitBreakerFactory circuitBreakerFactory;

    public EmailService(JavaMailSender mailSender, CircuitBreakerFactory circuitBreakerFactory) {
        this.mailSender = mailSender;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    public void sendEmail(String to, String subject, String body) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("emailService");

        circuitBreaker.run(() -> {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(to);
                message.setSubject(subject);
                message.setText(body);
                message.setFrom("sorohov344@gmail.com");

                mailSender.send(message);
                System.out.println("Email отправлен на: " + to);

                return null;

            } catch (Exception e) {
                System.err.println("Ошибка отправки email: " + e.getMessage());
                throw new RuntimeException("Ошибка отправки email", e);
            }
        }, throwable -> {
            System.err.println("Email service unavailable. Message to " + to + " was not sent.");

            return null;
        });
    }
}