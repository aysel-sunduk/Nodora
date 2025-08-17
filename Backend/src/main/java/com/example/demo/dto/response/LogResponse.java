package com.example.demo.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.OffsetDateTime; // TIMESTAMP WITH TIME ZONE i\u00e7in
import java.util.Map; // Map import edildi

/**
 * Log kay\u0131tlar\u0131n\u0131 d\u00f6nd\u00fcrmek i\u00e7in kullan\u0131lan yan\u0131t DTO'su.
 * \u0130stemciye g\u00f6nderilen log verilerini standartla\u015Ft\u0131r\u0131r.
 */
@Data // Lombok: Getter, Setter, equals, hashCode, toString metodlar\u0131n\u0131 otomatik olu\u015Fturur
@NoArgsConstructor // Lombok: Arg\u00fcmans\u0131z constructor'u otomatik olu\u015Fturur
public class LogResponse {
    private Long id;
    private String logLevel;
    private String message;
    private OffsetDateTime timestamp; // ZonedDateTime yerine OffsetDateTime kullan\u0131ld\u0131
    private String source;
    private String exception;
    private Map<String, Object> additionalData; // String yerine Map<String, Object>
    private Integer memberId; // Integer olarak kalmal\u0131
    private String ipAddress;
    private String requestPath;

    // T\u00fcm alanlar\u0131 alan constructor
    public LogResponse(Long id, Map<String, Object> additionalData, String exception, String ipAddress,
                       String logLevel, Integer memberId, String message, String requestPath,
                       String source, OffsetDateTime timestamp) {
        this.id = id;
        this.additionalData = additionalData;
        this.exception = exception;
        this.ipAddress = ipAddress;
        this.logLevel = logLevel;
        this.memberId = memberId;
        this.message = message;
        this.requestPath = requestPath;
        this.source = source;
        this.timestamp = timestamp;
    }
}