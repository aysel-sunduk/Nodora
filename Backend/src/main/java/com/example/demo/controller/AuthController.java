package com.example.demo.controller;

import com.example.demo.dto.request.*;
import com.example.demo.dto.response.*;
import com.example.demo.model.members.Member;
import com.example.demo.repository.MemberRepository;
import com.example.demo.service.AuthService;
import com.example.demo.service.EmailService;
import com.example.demo.service.GoogleAuthService;
import com.example.demo.service.GmailOAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize; // Yeni import

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final GoogleAuthService googleAuthService;
    private final MemberRepository memberRepository;
    private final EmailService emailService;
    private final GmailOAuthService gmailOAuthService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = authService.signup(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/google")
    public ResponseEntity<GoogleAuthResponse> googleAuth(@Valid @RequestBody GoogleAuthRequest request) {
        try {
            GoogleAuthResponse response = googleAuthService.authenticateWithGoogle(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Google OAuth endpoint'inde hata: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new GoogleAuthResponse(null, null, "Google ile giriş yapılamadı: " + e.getMessage(), false));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            String resultMessage = authService.forgotPassword(request.getEmail());
            return ResponseEntity.ok(new ForgotPasswordResponse(resultMessage));
        } catch (Exception e) {
            log.error("Forgot password endpoint'inde hata: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new ForgotPasswordResponse(e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponse> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            String resultMessage = authService.resetPassword(
                    request.getEmail(),
                    request.getTemporaryPassword(),
                    request.getNewPassword(),
                    request.getConfirmPassword()
            );
            return ResponseEntity.ok(new ResetPasswordResponse(resultMessage));
        } catch (Exception e) {
            log.error("Reset password endpoint'inde hata: {}", e.getMessage(), e);
            return ResponseEntity.badRequest()
                    .body(new ResetPasswordResponse(e.getMessage(), false));
        }
    }

    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')") // Sadece ADMIN rolüne sahip kullanıcılar erişebilir
    public ResponseEntity<List<Member>> getAllUsers() {
        List<Member> users = memberRepository.findAll();
        return ResponseEntity.ok(users);
    }
    /**
     * Basit test endpoint'i - JWT sorunlarını debug etmek için
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> testEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Auth endpoint çalışıyor");
        response.put("time", String.valueOf(System.currentTimeMillis()));
        response.put("gmailAPI", "devre dışı");
        response.put("smtpService", emailService.isAvailable() ? "aktif" : "devre dışı");
        response.put("googleOAuth", "aktif");
        response.put("googleClientId", googleAuthService != null ? "yüklendi" : "yüklenmedi");
        response.put("gmailOAuth", gmailOAuthService.isAvailable() ? "aktif" : "devre dışı");
        return ResponseEntity.ok(response);
    }

    /**
     * Google OAuth test endpoint'i
     */
    @GetMapping("/google/test")
    public ResponseEntity<Map<String, String>> googleTestEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Google OAuth endpoint çalışıyor");
        response.put("endpoint", "/api/auth/google");
        response.put("method", "POST");
        return ResponseEntity.ok(response);
    }

    /**
     * Google OAuth debug endpoint'i - gelen veriyi kontrol etmek için
     */
    @PostMapping("/google/debug")
    public ResponseEntity<Map<String, Object>> googleDebugEndpoint(@RequestBody GoogleAuthRequest request) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "debug");
        response.put("idToken", request.getIdToken() != null ? request.getIdToken().substring(0, Math.min(50, request.getIdToken().length())) + "..." : "null");
        response.put("accessToken", request.getAccessToken() != null ? request.getAccessToken().substring(0, Math.min(50, request.getAccessToken().length())) + "..." : "null");
        response.put("idTokenLength", request.getIdToken() != null ? request.getIdToken().length() : 0);
        response.put("accessTokenLength", request.getAccessToken() != null ? request.getAccessToken().length() : 0);
        return ResponseEntity.ok(response);
    }

    /**
     * Gmail OAuth 2.0 ile test e-posta gönderme endpoint'i
     */
    @PostMapping("/test-email")
    public ResponseEntity<Map<String, String>> testEmail(
            @RequestParam String toEmail,
            @RequestParam String fromEmail,
            @RequestParam(required = false, defaultValue = "Nodora Test") String subject,
            @RequestParam(required = false, defaultValue = "Bu bir test mesajıdır.") String message) {

        Map<String, String> response = new HashMap<>();
        try {
            System.out.println("🧪 Gmail OAuth test email gönderimi başlatılıyor...");
            gmailOAuthService.sendEmail(toEmail, subject, message, fromEmail);
            response.put("status", "success");
            response.put("message", "Test e-posta başarıyla gönderildi!");
            response.put("to", toEmail);
            response.put("from", fromEmail);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("❌ Test email gönderim hatası: " + e.getMessage());
            response.put("status", "error");
            response.put("message", "E-posta gönderim hatası: " + e.getMessage());
            response.put("error", e.getClass().getSimpleName());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * Rastgele şifre oluşturan yardımcı metot
     * @param length Oluşturulacak şifrenin uzunluğu
     * @return Rastgele oluşturulmuş şifre
     */
    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    /**
     * E-posta gönderen yardımcı metot (Gmail API öncelikli, SMTP fallback)
     * @param email Gönderilecek e-posta adresi
     * @param newPassword Gönderilecek yeni şifre
     * @return Sonuç mesajı
     */
    private String sendPasswordEmail(String email, String newPassword) {
        String subject = "Nodora - Şifre Sıfırlama";
        String body = "Merhaba,\n\n" +
                "Nodora hesabınız için şifre sıfırlama talebiniz alınmıştır.\n\n" +
                "Yeni şifreniz: " + newPassword + "\n\n" +
                "Güvenlik nedeniyle bu şifreyi ilk girişinizde değiştirmeniz önerilir.\n\n" +
                "Bu e-postayı siz talep etmediyseniz, lütfen derhal bizimle iletişime geçin.\n\n" +
                "İyi günler,\n" +
                "Nodora Ekibi";

        if (gmailOAuthService.isAvailable()) {
            try {
                System.out.println("🎯 Gmail OAuth API ile e-posta gönderimi deneniyor...");
                String fromEmail = "frontendproje@gmail.com";
                gmailOAuthService.sendEmail(email, subject, body, fromEmail);
                return "Yeni şifreniz e-posta adresinize gönderildi. [Gmail API]";
            } catch (Exception e) {
                System.err.println("🚨 Gmail OAuth API HATA: " + e.getMessage());
                if (e.getCause() != null) {
                    System.err.println("   Cause: " + e.getCause().getMessage());
                }
                System.out.println("🔄 Gmail API başarısız, SMTP'ye geçiliyor...");
            }
        } else {
            System.out.println("⚠️ Gmail OAuth servis mevcut değil, SMTP deneniyor...");
        }

        if (emailService.isAvailable()) {
            try {
                System.out.println("🎯 Outlook SMTP ile e-posta gönderimi deneniyor...");
                emailService.sendEmail(email, subject, body);
                return "Yeni şifreniz e-posta adresinize gönderildi. [Outlook SMTP - Fallback]";
            } catch (Exception e) {
                System.err.println("🚨 Outlook SMTP DETAYLI HATA:");
                System.err.println("   Hata türü: " + e.getClass().getSimpleName());
                System.err.println("   Hata mesajı: " + e.getMessage());
                if (e.getCause() != null) {
                    System.err.println("   Cause: " + e.getCause().getMessage());
                }
                return "E-posta gönderim hatası: Hem Gmail API hem de SMTP başarısız oldu. Teknik ekip bilgilendirildi.";
            }
        } else {
            return "E-posta servisi mevcut değil. Teknik ekiple iletişime geçin.";
        }
    }
}
