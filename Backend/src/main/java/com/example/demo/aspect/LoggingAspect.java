package com.example.demo.aspect;

import com.example.demo.dto.request.LogRequest; // LogRequest DTO'sunu import edin
import com.example.demo.dto.request.LoginRequest; // LoginRequest DTO'sunu import edin
import com.example.demo.model.members.Member; // Member entity'sini import edin
import com.example.demo.service.LogsService; // LogsService'i import edin
// import com.example.demo.model.logs.Logs; // Logs entity'si burada doğrudan kullan\u0131lm\u0131yor, yorum sat\u0131r\u0131na al\u0131nd\u0131
import com.example.demo.repository.MemberRepository; // MemberRepository'yi import edin
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Uygulama genelinde (Controller, Service, Repository katmanlar\u0131nda) metot \u00e7a\u011fr\u0131lar\u0131n\u0131 loglamak i\u00e7in kullan\u0131lan Aspect s\u0131n\u0131f\u0131.
 * Spring AOP kullanarak metot y\u00fcr\u00fctmelerini keser ve log kay\u0131tlar\u0131 olu\u015Fturur.
 * Ba\u015Far\u0131l\u0131 ve hatal\u0131 istekler i\u00e7in ayr\u0131 loglama yapar.
 * Ayr\u0131ca, kimlik do\u011frulama bilgilerinden memberId'yi almaya \u00e7al\u0131\u015F\u0131r.
 */
@Aspect
@Component
@Slf4j // Lombok: Otomatik olarak bir Logger nesnesi olu\u015Fturur (log ad\u0131nda)
public class    LoggingAspect {

    private final LogsService logsService; // LogsService kullan\u0131ld\u0131
    private final MemberRepository memberRepository;

    @Autowired
    public LoggingAspect(LogsService logsService, MemberRepository memberRepository) {
        this.logsService = logsService;
        this.memberRepository = memberRepository;
    }

    /**
     * Controller katman\u0131ndaki t\u00fcm metotlar\u0131 sarar ve loglama yapar.
     * Gelen iste\u011fi, y\u00fcr\u00fctme s\u00fcresini, ba\u015Far\u0131s\u0131z olursa hatay\u0131 loglar.
     * Ayr\u0131ca, e\u011fer varsa kullan\u0131c\u0131n\u0131n (member) ID'sini de log kayd\u0131na ekler.
     *
     * @param joinPoint Metot y\u00fcr\u00fctme noktas\u0131
     * @return Metodun d\u00f6nd\u00fcrd\u00fc\u011f\u00fc de\u011fer
     * @throws Throwable Y\u00fcr\u00fctme s\u0131ras\u0131nda olu\u015Fan herhangi bir hata
     */
    @Around("execution(* com.example.demo.controller..*(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        HttpServletRequest request = null;
        // memberId'yi lambda i\u00e7inde de\u011fi\u015Ftirebilmek i\u00e7in AtomicReference kullan\u0131ld\u0131
        AtomicReference<Integer> memberIdRef = new AtomicReference<>(null);

        try {
            request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            if (authentication != null && authentication.isAuthenticated() && !("anonymousUser".equals(authentication.getPrincipal()))) {
                Object principal = authentication.getPrincipal();
                log.debug("DEBUG: LoggingAspect - Principal tipi: {}, De\u011feri: {}", principal.getClass().getName(), principal.toString());

                if (principal instanceof Member) {
                    memberIdRef.set(((Member) principal).getMemberId());
                } else if (principal instanceof UserDetails) {
                    String username = ((UserDetails) principal).getUsername();
                    log.debug("DEBUG: LoggingAspect - UserDetails username: {}", username);
                    Optional<Member> memberOptional = memberRepository.findByEmail(username);
                    memberOptional.ifPresent(member -> memberIdRef.set(member.getMemberId()));
                } else if (principal instanceof String) {
                    String username = (String) principal;
                    log.debug("DEBUG: LoggingAspect - String principal username: {}", username);
                    Optional<Member> memberOptional = memberRepository.findByEmail(username);
                    memberOptional.ifPresent(member -> memberIdRef.set(member.getMemberId()));
                }
            } else {
                log.debug("DEBUG: LoggingAspect - Kimlik do\u011frulama yok veya anonim kullan\u0131c\u0131.");
            }
        } catch (IllegalStateException e) {
            log.warn("Request context not found for controller aspect. This may be expected for async operations.");
        }

        // Konsol logu
        log.info("🎯 [CONTROLLER] {}.{} called with args: {}",
                className, methodName, Arrays.toString(args));

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            // Konsol logu
            log.info("✅ [CONTROLLER] {}.{} completed successfully in {}ms",
                    className, methodName, executionTime);

            if (request != null) {
                Map<String, Object> additionalData = new HashMap<>();
                additionalData.put("executionTimeMs", executionTime);
                additionalData.put("method", request.getMethod());

                // LogRequest DTO kullanarak loglama
                LogRequest logRequest = new LogRequest();
                logRequest.setLogLevel("INFO");
                logRequest.setMessage("Request handled successfully for " + request.getRequestURI());
                logRequest.setSource(className); // Kaynak olarak controller s\u0131n\u0131f ad\u0131
                logRequest.setIpAddress(request.getRemoteAddr());
                logRequest.setRequestPath(request.getRequestURI());
                logRequest.setMemberId(memberIdRef.get());
                logRequest.setAdditionalData(additionalData);

                logsService.createLog(logRequest); // logsService kullan\u0131ld\u0131
            }
            return result;

        } catch (Exception ex) {
            long executionTime = System.currentTimeMillis() - startTime;

            log.error("❌ [CONTROLLER] {}.{} failed after {}ms with error: {}",
                    className, methodName, executionTime, ex.getMessage(), ex);

            if (request != null) {
                // Hatal\u0131 login denemesi i\u00e7in \u00f6zel durum
                if ("login".equals(methodName) && args.length > 0) {
                    Object loginRequestObj = args[0]; // Argument olarak LoginRequest bekleniyor
                    if (loginRequestObj instanceof LoginRequest) {
                        String email = ((LoginRequest) loginRequestObj).getEmail();
                        log.debug("DEBUG: LoggingAspect - LoginRequest email: {}", email);
                        Optional<Member> memberOptional = memberRepository.findByEmail(email);
                        memberOptional.ifPresent(member -> memberIdRef.set(member.getMemberId()));
                    }
                }

                // Hata logu i\u00e7in LogRequest DTO kullanarak loglama
                LogRequest errorLogRequest = new LogRequest();
                errorLogRequest.setLogLevel("ERROR");
                errorLogRequest.setMessage("Exception in " + methodName + ": " + ex.getMessage());
                errorLogRequest.setSource(className);
                errorLogRequest.setIpAddress(request.getRemoteAddr());
                errorLogRequest.setRequestPath(request.getRequestURI());
                errorLogRequest.setMemberId(memberIdRef.get());
                errorLogRequest.setException(ex.toString());
                errorLogRequest.setAdditionalData(Map.of("executionTimeMs", executionTime)); // Hata i\u00e7in ek veri

                logsService.createLog(errorLogRequest); // logsService kullan\u0131ld\u0131
            }

            throw ex;
        }
    }

    /**
     * Service katman\u0131ndaki t\u00fcm metotlar\u0131 sarar ve debug loglama yapar.
     *
     * @param joinPoint Metot y\u00fcr\u00fctme noktas\u0131
     * @return Metodun d\u00f6nd\u00fcrd\u00fc\u011f\u00fc de\u011fer
     * @throws Throwable Y\u00fcr\u00fctme s\u0131ras\u0131nda olu\u015Fan herhangi bir hata
     */
    @Around("execution(* com.example.demo.service..*(..))")
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.debug("🔧 [SERVICE] {}.{} started", className, methodName);

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            log.debug("✅ [SERVICE] {}.{} completed in {}ms",
                    className, methodName, executionTime);

            return result;
        } catch (Exception ex) {
            long executionTime = System.currentTimeMillis() - startTime;

            log.error("❌ [SERVICE] {}.{} failed after {}ms: {}",
                    className, methodName, executionTime, ex.getMessage(), ex);

            throw ex;
        }
    }

    /**
     * Repository katman\u0131ndaki t\u00fcm metotlar\u0131 sarar ve trace loglama yapar.
     *
     * @param joinPoint Metot y\u00fcr\u00fctme noktas\u0131
     * @return Metodun d\u00f6nd\u00fcrd\u00fc\u011f\u00fc de\u011fer
     * @throws Throwable Y\u00fcr\u00fctme s\u0131ras\u0131nda olu\u015Fan herhangi bir hata
     */
    @Around("execution(* com.example.demo.repository..*(..))")
    public Object logRepository(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.trace("🗃 [REPOSITORY] {}.{} executing", className, methodName);

        try {
            Object result = joinPoint.proceed();
            log.trace("✅ [REPOSITORY] {}.{} completed", className, methodName);
            return result;
        } catch (Exception ex) {
            log.error("❌ [REPOSITORY] {}.{} failed: {}",
                    className, methodName, ex.getMessage(), ex);
            throw ex;
        }
    }
}