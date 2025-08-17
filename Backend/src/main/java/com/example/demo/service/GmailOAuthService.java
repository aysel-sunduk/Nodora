package com.example.demo.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.GeneralSecurityException;
import java.util.Base64;
import java.util.Collections;
import java.util.Properties;

/**
 * Gmail OAuth 2.0 kullanarak e-posta gönderen servis sınıfı.
 * Kullanıcı authorization gerektirir - tarayıcıda Google'a giriş yapar.
 */
@Service
public class GmailOAuthService {

    private static final String APPLICATION_NAME = "Nodora Backend";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    @Value("${google.oauth.client.id}")
    private String clientId;

    @Value("${google.oauth.client.secret}")
    private String clientSecret;

    @Value("${google.oauth.redirect.uri:http://localhost:8080/oauth2/callback}")
    private String redirectUri;

    private Gmail gmailService;

    /**
     * OAuth 2.0 ile Gmail API servisini başlatır
     */
    private Gmail getGmailService() throws IOException, GeneralSecurityException {
        if (gmailService == null) {
            System.out.println("🔄 Gmail OAuth 2.0 servisi başlatılıyor...");
            System.out.println("📁 Client ID: " + clientId.substring(0, 15) + "...");

            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

            // Google Client Secrets oluştur
            GoogleClientSecrets clientSecrets = createClientSecrets();

            // OAuth 2.0 flow oluştur
            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, JSON_FACTORY, clientSecrets, Collections.singletonList(GmailScopes.GMAIL_SEND))
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                    .setAccessType("offline")
                    .build();

            // Credential al (tarayıcıda authorization gerekir)
            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

            System.out.println("✅ OAuth 2.0 authorization tamamlandı!");

            // Gmail service oluştur
            gmailService = new Gmail.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName(APPLICATION_NAME)
                    .build();

            System.out.println("✅ Gmail OAuth servisi başarıyla oluşturuldu!");
        }
        return gmailService;
    }

    /**
     * Google Client Secrets objesi oluşturur
     */
    private GoogleClientSecrets createClientSecrets() throws IOException {
        GoogleClientSecrets.Details details = new GoogleClientSecrets.Details();
        details.setClientId(clientId);
        details.setClientSecret(clientSecret);
        details.setRedirectUris(Collections.singletonList(redirectUri));

        GoogleClientSecrets clientSecrets = new GoogleClientSecrets();
        clientSecrets.setInstalled(details);

        return clientSecrets;
    }

    /**
     * E-posta gönderir (OAuth 2.0 ile)
     * @param toEmail Alıcı e-posta adresi
     * @param subject E-posta konusu
     * @param bodyText E-posta içeriği
     * @param fromEmail Gönderen e-posta (OAuth'u yapan kullanıcının emaili)
     * @throws Exception E-posta gönderimi sırasında hata oluşursa
     */
    public void sendEmail(String toEmail, String subject, String bodyText, String fromEmail) throws Exception {
        try {
            System.out.println("📧 Gmail OAuth 2.0 ile e-posta gönderiliyor...");
            System.out.println("   From: " + fromEmail + " (OAuth User)");
            System.out.println("   To: " + toEmail);
            System.out.println("   Subject: " + subject);

            Gmail service = getGmailService();
            MimeMessage emailContent = createEmail(toEmail, fromEmail, subject, bodyText);
            Message message = createMessageWithEmail(emailContent);

            System.out.println("🚀 Gmail API'ye gönderim talebi yapılıyor...");
            Message result = service.users().messages().send("me", message).execute();

            System.out.println("✅ Gmail API Response ID: " + result.getId());
            System.out.println("✅ Gmail OAuth ile e-posta başarıyla gönderildi: " + toEmail);

        } catch (Exception e) {
            System.err.println("❌ Gmail OAuth e-posta gönderim hatası: " + e.getMessage());
            System.err.println("❌ Hata türü: " + e.getClass().getSimpleName());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * MimeMessage oluşturur
     */
    private MimeMessage createEmail(String to, String from, String subject, String bodyText) throws MessagingException {
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipient(jakarta.mail.Message.RecipientType.TO, new InternetAddress(to));
        email.setSubject(subject);
        email.setText(bodyText);

        return email;
    }

    /**
     * Gmail API Message objesi oluşturur
     */
    private Message createMessageWithEmail(MimeMessage emailContent) throws MessagingException, IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.getUrlEncoder().encodeToString(bytes);

        Message message = new Message();
        message.setRaw(encodedEmail);

        return message;
    }

    /**
     * Gmail OAuth servisinin hazır olup olmadığını kontrol eder
     */
    public boolean isAvailable() {
        try {
            if (clientId == null || clientId.trim().isEmpty()) {
                System.out.println("⚠️ Gmail OAuth Client ID bulunamadı");
                return false;
            }

            if (clientSecret == null || clientSecret.trim().isEmpty()) {
                System.out.println("⚠️ Gmail OAuth Client Secret bulunamadı");
                return false;
            }

            System.out.println("✅ Gmail OAuth konfigürasyonu mevcut");
            return true;
        } catch (Exception e) {
            System.out.println("⚠️ Gmail OAuth mevcut değil: " + e.getMessage());
            return false;
        }
    }
} 