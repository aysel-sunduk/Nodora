package com.example.demo.dto.response;

public class ResetPasswordResponse {
    private String message;
    private boolean success;

    public ResetPasswordResponse() {
    }

    public ResetPasswordResponse(String message) {
        this.message = message;
        this.success = true;
    }

    public ResetPasswordResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}