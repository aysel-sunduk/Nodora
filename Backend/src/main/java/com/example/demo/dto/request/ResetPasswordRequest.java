package com.example.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ResetPasswordRequest {

    @NotBlank(message = "Email boş olamaz")
    @Email(message = "Geçerli bir email adresi giriniz")
    private String email;

    @NotBlank(message = "Geçici şifre boş olamaz")
    private String temporaryPassword;

    @NotBlank(message = "Yeni şifre boş olamaz")
    @Size(min = 6, message = "Yeni şifre en az 6 karakter olmalıdır")
    private String newPassword;

    @NotBlank(message = "Şifre tekrarı boş olamaz")
    private String confirmPassword;

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getTemporaryPassword() { return temporaryPassword; }
    public void setTemporaryPassword(String temporaryPassword) { this.temporaryPassword = temporaryPassword; }
    
    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}