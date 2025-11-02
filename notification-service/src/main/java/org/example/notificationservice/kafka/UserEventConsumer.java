package org.example.notificationservice.kafka;

import org.example.notificationservice.dto.UserEventDto;
import org.example.notificationservice.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(UserEventConsumer.class);
    private final EmailService emailService;

    public UserEventConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "user-events", groupId = "notification-service")
    public void consumeUserEvent(UserEventDto userEvent) {
        logger.info("Received user event: {} for user: {}", userEvent.getOperation(), userEvent.getEmail());

        try {
            if ("CREATE".equals(userEvent.getOperation())) {
                String subject = "Добро пожаловать!";
                String body = "Здравствуйте, " + userEvent.getUserName() + "! Ваш аккаунт на сайте ваш сайт был успешно создан.";
                emailService.sendEmail(userEvent.getEmail(), subject, body);

            } else if ("DELETE".equals(userEvent.getOperation())) {
                String subject = "Аккаунт удален";
                String body = "Здравствуйте, " + userEvent.getUserName() + "! Ваш аккаунт был удалён.";
                emailService.sendEmail(userEvent.getEmail(), subject, body);
            }

            logger.info("Successfully processed user event: {}", userEvent.getOperation());

        } catch (Exception e) {
            logger.error("Error processing user event: {}", userEvent.getOperation(), e);
        }
    }
}