package com.example.demo.model.logs; // Paket yolunuza g\u00f6re d\u00fczenlendi

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime; // TIMESTAMP WITH TIME ZONE i\u00e7in
import java.time.ZoneOffset; // UTC zaman dilimi i\u00e7in
import java.util.Map; // JSONB i\u00e7in

/**
 * Log Entity, PostgreSQL'deki 'logs' tablosunu temsil eder.
 * Merkezi loglama sistemi i\u00e7in tasarlanm\u0131\u015Ft\u0131r.
 * - id: BIGSERIAL PRIMARY KEY
 * - additional_data: JSONB
 * - exception: TEXT
 * - ip_address: VARCHAR(50)
 * - log_level: VARCHAR(20) NOT NULL
 * - member_id: INTEGER
 * - message: TEXT
 * - request_path: VARCHAR(255)
 * - source: VARCHAR(255) NOT NULL
 * - timestamp: TIMESTAMP WITH TIME ZONE DEFAULT NOW()
 */
@Entity
@Table(name = "logs")
@Data // Lombok: Getter, Setter, equals, hashCode, toString metodlar\u0131n\u0131 otomatik olu\u015Fturur
@NoArgsConstructor // Lombok: Arg\u00fcmans\u0131z constructor'u otomatik olu\u015Fturur
public class Logs { // S\u0131n\u0131f ad\u0131n\u0131 Logs olarak g\u00fcncelledim

    private static final Logger logger = LoggerFactory.getLogger(Logs.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Otomatik artan birincil anahtar
    private Long id;

    @Column(name = "log_level", length = 20, nullable = false) // VARCHAR(20) NOT NULL
    private String logLevel;

    @Column(name = "message", columnDefinition = "TEXT") // TEXT
    private String message;

    @Column(name = "timestamp") // TIMESTAMP WITH TIME ZONE
    private OffsetDateTime timestamp;

    @Column(name = "source", length = 255, nullable = false) // VARCHAR(255) NOT NULL
    private String source;

    @Column(name = "exception", columnDefinition = "TEXT") // TEXT
    private String exception;

    @JdbcTypeCode(SqlTypes.JSON) // Hibernate'e bu alan\u0131n JSONB olarak i\u015flenmesi gerekti\u011fini s\u0131yler
    @Column(name = "additional_data", columnDefinition = "jsonb") // JSONB
    private Map<String, Object> additionalData; // String yerine Map<String, Object>

    @Column(name = "member_id") // INTEGER
    private Integer memberId; // member_id tipi Integer

    @Column(name = "ip_address", length = 50) // VARCHAR(50)
    private String ipAddress;

    @Column(name = "request_path", length = 255) // VARCHAR(255)
    private String requestPath;

    /**
     * Entity veritaban\u0131na kaydedilmeden \u00f6nce otomatik olarak \u00e7al\u0131\u015Fan metot.
     * timestamp alan\u0131 null ise, o anki UTC zaman\u0131n\u0131 atar.
     */
    @PrePersist
    protected void onCreate() {
        logger.debug("DEBUG: @PrePersist metodu tetiklendi.");
        if (this.timestamp == null) {
            this.timestamp = OffsetDateTime.now(ZoneOffset.UTC);
            logger.debug("DEBUG: Timestamp null oldu\u011fu i\u00e7in atand\u0131: {}", this.timestamp);
        } else {
            logger.debug("DEBUG: Timestamp zaten ayarlanm\u0131\u015Ft\u0131: {}", this.timestamp);
        }
    }
}