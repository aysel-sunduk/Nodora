package com.example.demo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Şifre sıfırlama talebinin sonucunu temsil eden Response DTO sınıfı.
 * Bu sınıf şifre sıfırlama işleminin durumunu frontend'e bildirmek için kullanılır.
 * İşlem başarılı olduğunda veya hata oluştuğunda uygun mesajı taşır.
 */
@Data
@AllArgsConstructor
public class ForgotPasswordResponse {
    
    /**
     * Şifre sıfırlama işleminin sonucunu açıklayan mesaj
     * Başarılı durumda: "Yeni şifreniz e-posta adresinize gönderildi."
     * Hatalı durumda: "Bu e-posta adresi ile kayıtlı kullanıcı bulunamadı."
     */
    private String message;
}