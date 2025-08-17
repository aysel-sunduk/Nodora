package com.example.demo.controller;

import com.example.demo.dto.request.LogRequest;
import com.example.demo.dto.response.LogResponse;
import com.example.demo.service.LogsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // PreAuthorize i\u00e7in import
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Merkezi loglama sistemi i\u00e7in REST API endpoint'lerini y\u00f6neten Controller s\u0131n\u0131f\u0131.
 * Log kayd\u0131 olu\u015Fturma ve loglar\u0131 sorgulama i\u015Flemlerini sunar.
 */
@RestController
@RequestMapping("/api/v1/logs")
public class LogsController {

    private final LogsService logsService;

    @Autowired
    public LogsController(LogsService logsService) {
        this.logsService = logsService;
    }

    /**
     * Yeni bir log kayd\u0131 olu\u015Fturur.
     * Bu endpoint, API'nin kendisi taraf\u0131ndan \u00e7a\u011fr\u0131lmak yerine,
     * merkezi loglama i\u00e7in d\u0131\u015F sistemlerden log almak amac\u0131yla kullan\u0131labilir.
     * (Bu endpoint'e d\u0131\u015Fardan eri\u015Fim i\u00e7in ayr\u0131ca yetkilendirme mekanizmas\u0131 gerekebilir.)
     *
     * @param logRequest Olu\u015Fturulacak log kayd\u0131n\u0131n verileri
     * @return Kaydedilen logun detaylar\u0131 ve HTTP 201 Created durumu
     */
    @PostMapping
    // Bu endpoint genellikle sadece i\u00e7 sistemler taraf\u0131ndan kullan\u0131ld\u0131\u011f\u0131 i\u00e7in @PreAuthorize eklenmeyebilir,
    // ancak ihtiya\u00e7 duyulursa eklenebilir. Örne\u011fin: @PreAuthorize("hasRole('SYSTEM_LOGGER')")
    public ResponseEntity<LogResponse> createLog(@RequestBody LogRequest logRequest) {
        LogResponse createdLog = logsService.createLog(logRequest);
        return new ResponseEntity<>(createdLog, HttpStatus.CREATED);
    }

    /**
     * T\u00fcm log kay\u0131tlar\u0131n\u0131 getirir veya belirli kriterlere g\u00f6re filtreler.
     * Sadece ADMIN rol\u00fcne sahip kullan\u0131c\u0131lar eri\u015Febilir.
     *
     * @param source (Opsiyonel) Log kayna\u011f\u0131na g\u00f6re filtreleme
     * @param logLevel (Opsiyonel) Log seviyesine g\u00f6re filtreleme
     * @param memberId (Opsiyonel) \u00dcye ID'sine g\u00f6re filtreleme
     * @return Filtrelenmi\u015F log kay\u0131tlar\u0131n\u0131n listesi
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // Sadece ADMIN rol\u00fcne sahip kullan\u0131c\u0131lar eri\u015Febilir
    public ResponseEntity<List<LogResponse>> getAllLogs(
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String logLevel,
            @RequestParam(required = false) Integer memberId) {

        List<LogResponse> logs;

        // Daha kapsamlı filtreleme mantığı
        // Bu k\u0131s\u0131mda birden fazla parametreyi dikkate alarak sorgu yapmal\u0131y\u0131z.
        // Spring Data JPA'da dynamic query i\u00e7in Specification API veya QueryDSL daha uygun olur,
        // ancak mevcut findBy metotlar\u0131n\u0131 kullanarak bir kombinasyon olu\u015Fturaca\u011f\u0131z.
        if (source != null) {
            logs = logsService.getLogsBySource(source);
        } else if (logLevel != null) {
            logs = logsService.getLogsByLogLevel(logLevel);
        } else if (memberId != null) {
            logs = logsService.getLogsByMemberId(memberId);
        } else {
            // Hi\u00e7bir filtreleme yoksa t\u00fcm loglar\u0131 getir (dikkatli kullan\u0131n)
            // Performans i\u00e7in burada sayfalama (pagination) kullan\u0131lmas\u0131 \u00f6nerilir.
            logs = logsService.getAllLogs(); // Yeni eklenecek metot
        }

        // Birden fazla filtreleme durumunu ele al
        // \u015Fimdilik, sadece tekli filtreleri uygulayan veya hi\u00e7 uygulamayan basit bir if-else yap\u0131s\u0131
        // E\u011fer daha karma\u015F\u0131k kombinasyonlar isteniyorsa:
        // - Service katman\u0131nda tek bir 'searchLogs' metodu olu\u015Fturulup t\u00fcm parametreleri al\u0131nabilir.
        // - Veya birden \u00e7ok findBy kombinasyonu (findBySourceAndLogLevel gibi) Repository'ye eklenebilir.

        if (logs.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT); // 204 No Content
        }
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    /**
     * Belirli bir kayna\u011fa (source) ait log kay\u0131tlar\u0131n\u0131 listeler.
     * Sadece ADMIN rol\u00fcne sahip kullan\u0131c\u0131lar eri\u015Febilir.
     *
     * @param source Sorgulanacak log kayna\u011f\u0131 (URL parametresi olarak)
     * @return Log kay\u0131tlar\u0131n\u0131n listesi
     */
    @GetMapping("/by-source")
    @PreAuthorize("hasRole('ADMIN')") // Sadece ADMIN rol\u00fcne sahip kullan\u0131c\u0131lar eri\u015Febilir
    public ResponseEntity<List<LogResponse>> getLogsBySource(@RequestParam String source) {
        List<LogResponse> logs = logsService.getLogsBySource(source);
        if (logs.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    /**
     * Belirli bir log seviyesine (logLevel) ait log kay\u0131tlar\u0131n\u0131 listeler.
     * Sadece ADMIN rol\u00fcne sahip kullan\u0131c\u0131lar eri\u015Febilir.
     *
     * @param logLevel Sorgulanacak log seviyesi (URL parametresi olarak)
     * @return Log kay\u0131tlar\u0131n\u0131n listesi
     */
    @GetMapping("/by-level")
    @PreAuthorize("hasRole('ADMIN')") // Sadece ADMIN rol\u00fcne sahip kullan\u0131c\u0131lar eri\u015Febilir
    public ResponseEntity<List<LogResponse>> getLogsByLogLevel(@RequestParam String logLevel) {
        List<LogResponse> logs = logsService.getLogsByLogLevel(logLevel);
        if (logs.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    /**
     * Belirli bir üye ID'sine (memberId) ait log kayıtlarını listeler.
     * Sadece ADMIN rolüne sahip kullanıcılar erişebilir.
     *
     * @param memberId Sorgulanacak üye ID'si (URL parametresi olarak)
     * @return Log kayıtlarının listesi
     */
    @GetMapping("/by-member")
    @PreAuthorize("hasRole('ADMIN')") // Sadece ADMIN rolüne sahip kullan\u0131c\u0131lar eri\u015Febilir
    public ResponseEntity<List<LogResponse>> getLogsByMemberId(@RequestParam Integer memberId) {
        List<LogResponse> logs = logsService.getLogsByMemberId(memberId);
        if (logs.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }

    /**
     * Belirli bir üye ID'sine ve log seviyesine ait log kayıtlarını listeler.
     * Sadece ADMIN rolüne sahip kullanıcılar erişebilir.
     *
     * @param memberId Sorgulanacak üye ID'si (URL parametresi olarak)
     * @param logLevel Sorgulanacak log seviyesi (URL parametresi olarak)
     * @return Log kayıtlarının listesi
     */
    @GetMapping("/by-member-and-level")
    @PreAuthorize("hasRole('ADMIN')") // Sadece ADMIN rolüne sahip kullan\u0131c\u0131lar eri\u015Febilir
    public ResponseEntity<List<LogResponse>> getLogsByMemberIdAndLogLevel(
            @RequestParam Integer memberId,
            @RequestParam String logLevel) {
        List<LogResponse> logs = logsService.getLogsByMemberIdAndLogLevel(memberId, logLevel);
        if (logs.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(logs, HttpStatus.OK);
    }
}