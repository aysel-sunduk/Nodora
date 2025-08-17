package com.example.demo.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Map; // Map import edildi

/**
 * Log kayd\u0131 olu\u015Fturmak i\u00e7in kullan\u0131lan istek DTO'su.
 * API \u00fczerinden gelen log verilerini standartla\u015Ft\u0131r\u0131r.
 */
@Data // Lombok: Getter, Setter, equals, hashCode, toString metodlar\u0131n\u0131 otomatik olu\u015Fturur
@NoArgsConstructor // Lombok: Arg\u00fcmans\u0131z constructor'u otomatik olu\u015Fturur
@AllArgsConstructor // Lombok: T\u00fcm arg\u00fcmanl\u0131 constructor'u otomatik olu\u015Fturur
public class LogRequest {
    private String logLevel;
    private String message;
    private String source;
    private String ipAddress;
    private String requestPath;
    private Integer memberId; // Integer olarak kalmal\u0131
    private String exception;
    private Map<String, Object> additionalData; // String yerine Map<String, Object>
}