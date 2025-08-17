package com.example.demo.dto.response;

public class SignupResponse {
    private String message;

    public SignupResponse() {
    }

    public SignupResponse(String message) {
        this.message = message;
    }

    // Getter and Setter
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
 }
}