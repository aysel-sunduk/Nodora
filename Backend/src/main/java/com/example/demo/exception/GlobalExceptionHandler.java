package com.example.demo.exception;

import com.example.demo.dto.request.LogRequest;
import com.example.demo.service.LogsService;
import jakarta.servlet.http.HttpServletRequest; // HttpServletRequest import edildi
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Slf4j eklendi
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.RequestContextHolder; // RequestContextHolder import edildi
import org.springframework.web.context.request.ServletRequestAttributes; // ServletRequestAttributes import edildi

import java.util.HashMap;
import java.util.Map;
import java.util.Objects; // Objects import edildi

@ControllerAdvice
@RequiredArgsConstructor // LogsService'i otomatik enjekte etmek i\u00e7in
@Slf4j // Logger kullanmak i\u00e7in
public class GlobalExceptionHandler {

    private final LogsService logsService; // LogsService enjekte edildi

    /**
     * Kullan\u0131c\u0131 bulunamad\u0131 hatalar\u0131n\u0131 yakalar ve loglar.
     *
     * @param ex Yakalanan UsernameNotFoundException
     * @param request Mevcut HTTP iste\u011fi
     * @return Hata mesaj\u0131n\u0131 i\u00e7eren ResponseEntity
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleUsernameNotFoundException(UsernameNotFoundException ex, HttpServletRequest request) {
        log.warn("Kullan\u0131c\u0131 bulunamad\u0131: {}", ex.getMessage());

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Kullan\u0131c\u0131 bulunamad\u0131");
        errorResponse.put("message", ex.getMessage());

        // Log kayd\u0131 olu\u015Ftur
        LogRequest logRequest = new LogRequest();
        logRequest.setLogLevel("WARN"); // Kullan\u0131c\u0131 bulunamamas\u0131 ERROR yerine WARN olabilir
        logRequest.setMessage("Kullan\u0131c\u0131 bulunamad\u0131: " + ex.getMessage());
        logRequest.setSource(request.getRequestURI()); // Kaynak olarak istek URI'si
        logRequest.setException(ex.toString()); // Exception detay\u0131

        // IP adresi ve istek yolunu almaya \u00e7al\u0131\u015F
        try {
            HttpServletRequest currentRequest = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            logRequest.setIpAddress(currentRequest.getRemoteAddr());
            logRequest.setRequestPath(currentRequest.getRequestURI());
        } catch (IllegalStateException e) {
            log.warn("Request context not available for logging UsernameNotFoundException.");
        }

        logsService.createLog(logRequest);

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND); // 404 Not Found
    }

    /**
     * BadCredentialsException (hatal\u0131 giri\u015F) hatalar\u0131n\u0131 yakalar ve loglar.
     *
     * @param ex Yakalanan BadCredentialsException
     * @param request Mevcut HTTP iste\u011fi
     * @return Hata mesaj\u0131n\u0131 i\u00e7eren ResponseEntity
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleBadCredentialsException(BadCredentialsException ex, HttpServletRequest request) {
        log.error("Kimlik do\u011frulama hatas\u0131: {}", ex.getMessage(), ex);

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Kimlik Do\u011frulama Hatas\u0131");
        errorResponse.put("message", ex.getMessage());

        // Log kayd\u0131 olu\u015Ftur
        LogRequest logRequest = new LogRequest();
        logRequest.setLogLevel("ERROR");
        logRequest.setMessage("Kimlik do\u011frulama ba\u015Far\u0131s\u0131z: " + ex.getMessage());
        logRequest.setSource("AuthService"); // Hatan\u0131n kayna\u011f\u0131
        logRequest.setException(ex.toString()); // Exception detay\u0131

        // IP adresi ve istek yolunu almaya \u00e7al\u0131\u015F
        try {
            HttpServletRequest currentRequest = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            logRequest.setIpAddress(currentRequest.getRemoteAddr());
            logRequest.setRequestPath(currentRequest.getRequestURI());
            // memberId burada al\u0131namayabilir \u00e7\u00fcnk\u00fc kimlik do\u011frulamas\u0131 ba\u015Far\u0131s\u0131z oldu.
            // LoggingAspect zaten bunu yapmaya \u00e7al\u0131\u015F\u0131yor, ancak burada da loglamak \u00f6nemli.
        } catch (IllegalStateException e) {
            log.warn("Request context not available for logging BadCredentialsException.");
        }

        logsService.createLog(logRequest);

        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED); // 401 Unauthorized
    }

    /**
     * Di\u011fer t\u00fcm genel Exception'lar\u0131 yakalar ve loglar.
     *
     * @param ex Yakalanan Exception
     * @param request Mevcut HTTP iste\u011fi
     * @return Hata mesaj\u0131n\u0131 i\u00e7eren ResponseEntity
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Genel hata olu\u015Ftu: {}", ex.getMessage(), ex);

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Sunucu Hatas\u0131");
        errorResponse.put("message", "Beklenmeyen bir hata olu\u015Ftu.");

        // Log kayd\u0131 olu\u015Ftur
        LogRequest logRequest = new LogRequest();
        logRequest.setLogLevel("ERROR");
        logRequest.setMessage("Genel hata: " + ex.getMessage());
        logRequest.setSource(request.getRequestURI()); // Kaynak olarak istek URI'si
        logRequest.setException(ex.toString()); // Exception detay\u0131

        // IP adresi ve istek yolunu almaya \u00e7al\u0131\u015F
        try {
            HttpServletRequest currentRequest = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            logRequest.setIpAddress(currentRequest.getRemoteAddr());
            logRequest.setRequestPath(currentRequest.getRequestURI());
        } catch (IllegalStateException e) {
            log.warn("Request context not available for logging general exception.");
        }

        logsService.createLog(logRequest);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR); // 500 Internal Server Error
    }
}