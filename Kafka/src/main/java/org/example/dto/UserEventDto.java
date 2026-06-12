package org.example.dto;

public class UserEventDto {
    private String action;
    private String email;

    public UserEventDto() {}

    public UserEventDto(String action, String email) {
        this.action = action;
        this.email = email;
    }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
