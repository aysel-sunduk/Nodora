// src/main/java/com/example/demo/service/AuthService.java

package com.example.demo.service;

import com.example.demo.dto.request.LoginRequest;
import com.example.demo.dto.request.SignupRequest;
import com.example.demo.dto.response.LoginResponse;
import com.example.demo.dto.response.SignupResponse;
import com.example.demo.model.members.Member;
import com.example.demo.model.roles.Roles;
import com.example.demo.model.workspace_members.WorkspaceMember;
import com.example.demo.repository.MemberRepository;
import com.example.demo.repository.RoleRepository;
import com.example.demo.repository.WorkspaceMemberRepository;
import com.example.demo.security.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final WorkspaceMemberRepository workspaceMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final GmailOAuthService gmailOAuthService;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        log.info("Signup isteği: {}", request.getEmail());

        if (memberRepository.existsByEmail(request.getEmail())) {
            log.warn("Email zaten kayıtlı: {}", request.getEmail());
            return new SignupResponse("Bu email zaten kayıtlı!");
        }

        Member member = new Member();
        member.setName(request.getName());
        member.setSurname(request.getSurname());
        member.setEmail(request.getEmail());
        member.setMemberName(request.getMemberName());
        member.setPassword(passwordEncoder.encode(request.getPassword()));
        member.setIsAdmin(false);
        member.setMembersActive(true);
        Member savedMember = memberRepository.save(member);

        // Kullanıcı için GLOBAL MEMBER rolünü bul
        Roles memberRole = roleRepository.findByRoleNameAndScope("MEMBER", "GLOBAL")
                .orElseThrow(() -> new RuntimeException("GLOBAL MEMBER rolü bulunamadı!"));

        // Yeni kullanıcı için global rolü WorkspaceMember tablosuna ekle
        WorkspaceMember globalMember = new WorkspaceMember();
        globalMember.setMember(savedMember);
        globalMember.setRole(memberRole);
        globalMember.setCreatedAt(LocalDateTime.now());
        workspaceMemberRepository.save(globalMember);


        log.info("Kullanıcı ve GLOBAL MEMBER rolü başarıyla kaydedildi: {}", request.getEmail());
        return new SignupResponse("Kayıt başarılı");
    }

    public LoginResponse login(LoginRequest request) {
        try {
            log.info("Login isteği: {}", request.getEmail());
            Member member = memberRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> {
                        log.error("Kullanıcı bulunamadı: {}", request.getEmail());
                        return new UsernameNotFoundException("Kullanıcı bulunamadı");
                    });
            log.info("Kullanıcı bulundu: {}", request.getEmail());

            if (!passwordEncoder.matches(request.getPassword(), member.getPassword())) {
                log.error("Şifre hatalı: {}", request.getEmail());
                throw new BadCredentialsException("Şifre hatalı");
            }
            log.info("Şifre doğrulandı: {}", request.getEmail());

            String token = jwtUtil.generateToken(member.getEmail());
            log.info("JWT token oluşturuldu: {}", request.getEmail());

            Integer roleId = null;
            String roleName = null;

            // DÜZELTİLEN KISIM: Global rolü belirlemek için isAdmin bayrağını ve WorkspaceMember tablosunu kullan
            if (Boolean.TRUE.equals(member.getIsAdmin())) {
                log.info("Yönetici girişi. Rol: ADMIN");
                roleName = "ADMIN";
                // ADMIN rol ID'sini bulmaya çalış veya null bırak
                Roles adminRole = roleRepository.findByRoleNameAndScope("ADMIN", "GLOBAL")
                        .orElse(null);
                if (adminRole != null) {
                    roleId = adminRole.getRoleId();
                }
            } else {
                log.info("Normal kullanıcı girişi. Rolü WorkspaceMember tablosundan alınıyor.");
                // Normal kullanıcı için global MEMBER rolünü bul
                Optional<WorkspaceMember> globalMemberOpt = workspaceMemberRepository.findByMemberAndWorkspaceIdIsNull(member);

                if (globalMemberOpt.isPresent()) {
                    Roles globalRole = globalMemberOpt.get().getRole();
                    if (globalRole != null) {
                        roleId = globalRole.getRoleId();
                        roleName = globalRole.getRoleName();
                        log.info("Kullanıcının global rolü bulundu. Rol ID: {}, Rol Adı: {}", roleId, roleName);
                    } else {
                        log.error("Kullanıcının global rol nesnesi null. memberId: {}", member.getMemberId());
                        throw new RuntimeException("Kullanıcının global rolü bulunamadı veya rol bilgisi eksik!");
                    }
                } else {
                    log.error("Kullanıcının WorkspaceMember tablosunda global rol kaydı bulunamadı. memberId: {}", member.getMemberId());
                    throw new RuntimeException("Kullanıcının global rolü bulunamadı!");
                }
            }

            // LoginResponse nesnesini tüm bilgilerle oluştur ve döndür
            return new LoginResponse(
                    token,
                    member.getMemberId(),
                    roleId,
                    roleName
            );

        } catch (Exception e) {
            log.error("Login sırasında hata: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Transactional
    public String forgotPassword(String email) {
        Optional<Member> memberOptional = memberRepository.findByEmail(email);
        if (memberOptional.isEmpty()) {
            throw new RuntimeException("Bu e-posta adresi ile kayıtlı kullanıcı bulunamadı.");
        }

        Member member = memberOptional.get();
        String newPassword = generateRandomPassword(8);
        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);

        return sendPasswordEmail(member.getEmail(), newPassword);
    }

    @Transactional
    public String resetPassword(String email, String temporaryPassword, String newPassword, String confirmPassword) {
        Optional<Member> memberOptional = memberRepository.findByEmail(email);
        if (memberOptional.isEmpty()) {
            throw new RuntimeException("Bu e-posta adresi ile kayıtlı kullanıcı bulunamadı.");
        }

        Member member = memberOptional.get();
        if (!passwordEncoder.matches(temporaryPassword, member.getPassword())) {
            throw new BadCredentialsException("Geçici şifre hatalı.");
        }

        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("Yeni şifre ile şifre tekrarı uyuşmuyor.");
        }

        if (newPassword.length() < 6) {
            throw new RuntimeException("Yeni şifre en az 6 karakter olmalıdır.");
        }

        member.setPassword(passwordEncoder.encode(newPassword));
        memberRepository.save(member);
        log.info("Şifre başarıyla sıfırlandı: {}", email);

        return "Şifreniz başarıyla güncellendi. Artık yeni şifrenizle giriş yapabilirsiniz.";
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
