package com.example.demo.service;

import com.example.demo.dto.request.LogRequest;
import com.example.demo.dto.response.LogResponse;
import com.example.demo.model.logs.Logs; // Logs entity'si import edildi
import com.example.demo.repository.LogsRepository; // LogsRepository import edildi
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime; // OffsetDateTime kullan\u0131ld\u0131
import java.util.List;
import java.util.Map; // additionalData i\u00e7in Map import edildi
import java.util.stream.Collectors;

@Service
public class LogsService {

    private static final Logger logger = LoggerFactory.getLogger(LogsService.class);

    private final LogsRepository logsRepository;

    @Autowired
    public LogsService(LogsRepository logsRepository) {
        this.logsRepository = logsRepository;
    }

    /**
     * Dışarıdan gelen LogRequest DTO'su ile yeni bir log kaydı oluşturur.
     *
     * @param logRequest DTO türünde gelen log verisi
     * @return Kaydedilen logun yanıt DTO'su (LogResponse)
     */
    @Transactional
    public LogResponse createLog(LogRequest logRequest) {
        // DTO'dan Entity nesnesine dönüşüm (Mapping)
        Logs log = new Logs();
        log.setAdditionalData(logRequest.getAdditionalData()); // Artık Map<String, Object> olarak ayarlan\u0131yor
        log.setException(logRequest.getException());
        log.setIpAddress(logRequest.getIpAddress());
        log.setLogLevel(logRequest.getLogLevel());
        log.setMemberId(logRequest.getMemberId());
        log.setMessage(logRequest.getMessage());
        log.setRequestPath(logRequest.getRequestPath());
        log.setSource(logRequest.getSource());
        // log.setTimestamp(OffsetDateTime.now()); // BU SATIR KALDIRILDI - Timestamp atamas\u0131 Logs entity'sindeki @PrePersist taraf\u0131ndan yap\u0131lacak

        logger.debug("DEBUG: LogsService - createLog öncesi logEntity memberId: {}", log.getMemberId());
        logger.debug("DEBUG: LogsService - createLog öncesi logEntity timestamp: {}", log.getTimestamp());


        // Repository üzerinden kaydetme işlemi
        Logs savedLog = logsRepository.save(log);

        logger.debug("DEBUG: LogsService - logRepository.save çağrıldı, kaydedilen ID: {}", savedLog.getId());
        logger.debug("DEBUG: LogsService - Kaydedilen logun timestamp değeri: {}", savedLog.getTimestamp());
        logger.debug("DEBUG: LogsService - Kaydedilen logun memberId değeri: {}", savedLog.getMemberId());


        // Kaydedilen Entity nesnesini Response DTO'suna dönüştürme
        return new LogResponse(
                savedLog.getId(),
                savedLog.getAdditionalData(),
                savedLog.getException(),
                savedLog.getIpAddress(),
                savedLog.getLogLevel(),
                savedLog.getMemberId(),
                savedLog.getMessage(),
                savedLog.getRequestPath(),
                savedLog.getSource(),
                savedLog.getTimestamp()
        );
    }

    public List<LogResponse> getAllLogs() {
        List<Logs> logs = logsRepository.findAll();
        return logs.stream()
                .map(log -> new LogResponse(
                        log.getId(),
                        log.getAdditionalData(),
                        log.getException(),
                        log.getIpAddress(),
                        log.getLogLevel(),
                        log.getMemberId(),
                        log.getMessage(),
                        log.getRequestPath(),
                        log.getSource(),
                        log.getTimestamp()))
                .collect(Collectors.toList());
    }

    /**
     * Belirli bir log kaynağına göre log kayıtlarını getirir.
     *
     * @param source Log kaynağı
     * @return LogResponse DTO'larından oluşan bir liste
     */
    public List<LogResponse> getLogsBySource(String source) {
        List<Logs> logs = logsRepository.findBySource(source);
        return logs.stream()
                .map(log -> new LogResponse(
                        log.getId(),
                        log.getAdditionalData(),
                        log.getException(),
                        log.getIpAddress(),
                        log.getLogLevel(),
                        log.getMemberId(),
                        log.getMessage(),
                        log.getRequestPath(),
                        log.getSource(),
                        log.getTimestamp()))
                .collect(Collectors.toList());
    }

    /**
     * Belirli bir log seviyesine göre log kayıtlarını getirir.
     *
     * @param logLevel Log seviyesi
     * @return LogResponse DTO'larından oluşan bir liste
     */
    public List<LogResponse> getLogsByLogLevel(String logLevel) {
        List<Logs> logs = logsRepository.findByLogLevel(logLevel);
        return logs.stream()
                .map(log -> new LogResponse(
                        log.getId(),
                        log.getAdditionalData(),
                        log.getException(),
                        log.getIpAddress(),
                        log.getLogLevel(),
                        log.getMemberId(),
                        log.getMessage(),
                        log.getRequestPath(),
                        log.getSource(),
                        log.getTimestamp()))
                .collect(Collectors.toList());
    }

    /**
     * Belirli bir üye ID'sine (memberId) ait log kayıtlarını listeler.
     *
     * @param memberId Üye ID'si
     * @return LogResponse DTO'larından oluşan bir liste
     */
    public List<LogResponse> getLogsByMemberId(Integer memberId) {
        List<Logs> logs = logsRepository.findByMemberId(memberId);
        return logs.stream()
                .map(log -> new LogResponse(
                        log.getId(),
                        log.getAdditionalData(),
                        log.getException(),
                        log.getIpAddress(),
                        log.getLogLevel(),
                        log.getMemberId(),
                        log.getMessage(),
                        log.getRequestPath(),
                        log.getSource(),
                        log.getTimestamp()))
                .collect(Collectors.toList());
    }

    /**
     * Belirli bir üye ID'sine ve log seviyesine ait log kayıtlarını listeler.
     *
     * @param memberId Üye ID'si
     * @param logLevel Log seviyesi
     * @return LogResponse DTO'larından oluşan bir liste
     */
    public List<LogResponse> getLogsByMemberIdAndLogLevel(Integer memberId, String logLevel) {
        List<Logs> logs = logsRepository.findByMemberIdAndLogLevel(memberId, logLevel);
        return logs.stream()
                .map(log -> new LogResponse(
                        log.getId(),
                        log.getAdditionalData(),
                        log.getException(),
                        log.getIpAddress(),
                        log.getLogLevel(),
                        log.getMemberId(),
                        log.getMessage(),
                        log.getRequestPath(),
                        log.getSource(),
                        log.getTimestamp()))
                .collect(Collectors.toList());
    }
}