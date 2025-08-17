package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Outlook SMTP kullanarak e-posta gönderen servis sınıfı.
 * Spring Boot Mail Starter ile basit ve güvenilir e-posta gönderimi.
 * Gmail API yerine SMTP kullanır - daha basit ve sorunsuz.
 */
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * E-posta gönderir (Outlook SMTP ile)
     * @param toEmail Alıcı e-posta adresi
     * @param subject E-posta konusu
     * @param bodyText E-posta içeriği
     * @throws Exception E-posta gönderimi sırasında hata oluşursa
     */
    public void sendEmail(String toEmail, String subject, String bodyText) throws Exception {
        try {
            System.out.println("📧 Outlook SMTP ile e-posta gönderiliyor...");
            System.out.println("   From: " + fromEmail + " (Outlook)");
            System.out.println("   To: " + toEmail);
            System.out.println("   Subject: " + subject);
            
            // SimpleMailMessage oluştur
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(bodyText);
            
            System.out.println("🚀 Outlook SMTP'ye gönderim talebi yapılıyor...");
            
            // E-postayı gönder
            mailSender.send(message);
            
            System.out.println("✅ Outlook SMTP ile e-posta başarıyla gönderildi: " + toEmail);
            
        } catch (Exception e) {
            System.err.println("❌ Outlook SMTP e-posta gönderim hatası: " + e.getMessage());
            System.err.println("❌ Hata türü: " + e.getClass().getSimpleName());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * E-posta servisinin hazır olup olmadığını kontrol eder
     */
    public boolean isAvailable() {
        try {
            if (mailSender == null) {
                System.out.println("⚠️ JavaMailSender bulunamadı");
                return false;
            }
            
            if (fromEmail == null || fromEmail.trim().isEmpty()) {
                System.out.println("⚠️ From email adresi bulunamadı");
                return false;
            }
            
            System.out.println("✅ Outlook SMTP servisi hazır: " + fromEmail);
            return true;
        } catch (Exception e) {
            System.out.println("⚠️ Outlook SMTP servisi mevcut değil: " + e.getMessage());
            return false;
        }
    }
} 