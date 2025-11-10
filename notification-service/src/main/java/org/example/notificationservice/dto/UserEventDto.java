package org.example.notificationservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class UserEventDto {

    @Schema(description = "Тип операции с пользователем")
    private String operation;

    @Schema(description = "Email пользователя")
    private String email;

    @Schema(description = "Имя пользователя")
    private String userName;
    public UserEventDto() {}

    public UserEventDto(String operation, String email, String userName) {
        this.operation = operation;
        this.email = email;
        this.userName = userName;
    }

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
}
