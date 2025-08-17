package com.example.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * Şifre sıfırlama talebini temsil eden Request DTO sınıfı.
 * Bu sınıf frontend'den gelen şifre sıfırlama taleplerini almak için kullanılır.
 * Kullanıcı sadece email adresini göndererek şifre sıfırlama talebinde bulunabilir.
 */
@Data
public class ForgotPasswordRequest {
    
    /**
     * Şifresi sıfırlanacak kullanıcının email adresi
     * - Boş olamaz (@NotBlank validation)
     * - Geçerli email formatında olmalı (@Email validation)
     */
    @NotBlank(message = "Email adresi boş olamaz")
    @Email(message = "Geçerli bir email adresi girmelisiniz")
    private String email;
}