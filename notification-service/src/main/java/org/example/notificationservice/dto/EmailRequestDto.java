package org.example.notificationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class EmailRequestDto {

    @Schema(description = "Email адрес получателя")
    private String to;

    @Schema(description = "Тема письма")
    private String subject;

    @Schema(description = "Текст пользователя")
    private String body;

    public EmailRequestDto() {}

    public EmailRequestDto(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
