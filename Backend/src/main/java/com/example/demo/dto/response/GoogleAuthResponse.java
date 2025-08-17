package com.example.demo.dto.response;

public class GoogleAuthResponse {
    private String token;
    private Integer memberId;
    private String message;
    private Boolean success;

    // Constructors
    public GoogleAuthResponse(String token, Integer memberId, String message, Boolean success) {
        this.token = token;
        this.memberId = memberId;
        this.message = message;
        this.success = success;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }
}