package org.example.notificationservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.notificationservice.dto.EmailRequestDto;
import org.example.notificationservice.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notification API", description = "API для отправки email уведомлений")
public class EmailController {

    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @Operation(summary = "Отправить email",
    description = "Позволяет отправить Email")
    @PostMapping("/email")
    public void sendEmail(@RequestBody EmailRequestDto emailRequest) {
        emailService.sendEmail(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getBody());
    }

    @Operation(summary = "Отправить уведомление о создании пользователя",
    description = "Отправляет уведомление о создании пользователя")
    @PostMapping("/user-created")
    public void sendUserCreatedEmail(
            @RequestParam String email,
            @RequestParam String userName) {
        String subject = "Добро пожаловать!";
        String body = "Здравствуйте, " + userName + "! Ваш аккаунт на сайте был успешно создан.";
        emailService.sendEmail(email, subject, body);
    }

    @Operation(summary = "Отправить уведомление об удалении пользователя",
    description = "Отправляет уведомление об удалении пользователя")
    @PostMapping("/user-deleted")
    public void sendUserDeletedEmail(
            @RequestParam String email,
            @RequestParam String userName) {
        String subject = "Аккаунт удален";
        String body = "Здравствуйте, " + userName + "! Ваш аккаунт был удалён.";
        emailService.sendEmail(email, subject, body);
    }

    @Operation(summary = "Проверить работу сервиса",
    description = "Позволяет проверить работу сервиса")
    @GetMapping("/health")
    public String healthCheck() {
        return "Notification service is running";
    }
}