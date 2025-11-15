package org.example.notificationservice.kafka;

import org.example.notificationservice.dto.UserEventDto;
import org.example.notificationservice.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(UserEventConsumer.class);
    private final EmailService emailService;
    private final CircuitBreakerFactory circuitBreakerFactory;

    public UserEventConsumer(EmailService emailService, CircuitBreakerFactory circuitBreakerFactory) {
        this.emailService = emailService;
        this.circuitBreakerFactory = circuitBreakerFactory;
    }

    @KafkaListener(topics = "user-events", groupId = "notification-service")
    public void consumeUserEvent(UserEventDto userEvent) {
        CircuitBreaker circuitBreaker = circuitBreakerFactory.create("kafkaConsumer");

        circuitBreaker.run(() -> {
            logger.info("Received user event: {} for user: {}", userEvent.getOperation(), userEvent.getEmail());

            try {
                if ("CREATE".equals(userEvent.getOperation())) {
                    sendWelcomeEmail(userEvent.getEmail(), userEvent.getUserName());
                } else if ("DELETE".equals(userEvent.getOperation())) {
                    sendGoodbyeEmail(userEvent.getEmail(), userEvent.getUserName());
                }
                logger.info("Successfully processed user event: {}", userEvent.getOperation());
            } catch (Exception e) {
                logger.error("Error processing user event: {}", userEvent.getOperation(), e);
                throw e;
            }

            return null;
        }, throwable -> {
            logger.error("Circuit Breaker opened for Kafka consumer. Event {} for user {} was not processed.",
                    userEvent.getOperation(), userEvent.getEmail());

            return null;
        });
    }

    private void sendWelcomeEmail(String email, String userName) {
        String subject = "Добро пожаловать!";
        String body = "Здравствуйте, " + userName + "! Ваш аккаунт был успешно создан.";
        emailService.sendEmail(email, subject, body);
    }

    private void sendGoodbyeEmail(String email, String userName) {
        String subject = "Аккаунт удален";
        String body = "Здравствуйте, " + userName + "! Ваш аккаунт был удалён.";
        emailService.sendEmail(email, subject, body);
    }
}