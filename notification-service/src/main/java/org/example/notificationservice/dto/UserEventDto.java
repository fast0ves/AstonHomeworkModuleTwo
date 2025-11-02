package org.example.notificationservice.dto;

public class UserEventDto {
    private String operation;
    private String email;
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
