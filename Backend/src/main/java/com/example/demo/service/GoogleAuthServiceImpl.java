package com.example.demo.service;

import com.example.demo.dto.request.GoogleAuthRequest;
import com.example.demo.dto.response.GoogleAuthResponse;
import com.example.demo.model.members.Member;
import com.example.demo.repository.MemberRepository;
import com.example.demo.security.JwtUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoogleAuthServiceImpl implements GoogleAuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${google.oauth.client.id}")
    private String googleClientId;

    @Override
    public GoogleAuthResponse authenticateWithGoogle(GoogleAuthRequest request) {
        try {
            log.info("Google OAuth authentication başlatılıyor");

            // NOTE: Gerçek projede, idToken'ı doğrulamak ve kullanıcı bilgilerini almak için Google'ın API'leri kullanılmalıdır.
            // Bu örnek, basitleştirilmiş bir test senaryosudur.
            String email = "test@google.com";
            String givenName = "Google";
            String familyName = "User";

            log.info("Test kullanıcı bilgileri: {}", email);

            Optional<Member> existingMember = memberRepository.findByEmail(email);
            boolean isNewUser = false;
            Member member;

            if (existingMember.isPresent()) {
                member = existingMember.get();
                log.info("Mevcut kullanıcı bulundu: {}", email);
            } else {
                member = new Member();
                member.setEmail(email);
                member.setName(givenName);
                member.setSurname(familyName);
                member.setMemberName(email.split("@")[0]);
                member.setPassword(passwordEncoder.encode("google_oauth_" + System.currentTimeMillis()));
                member.setIsAdmin(false);
                member.setMembersActive(true);

                member = memberRepository.save(member);
                isNewUser = true;
                log.info("Yeni kullanıcı oluşturuldu: {}", email);
            }

            // YENİ YAPI: JWT token'ı sadece email ile oluştur.
            // Rol, scope gibi bilgiler token içinde tutulmuyor.
            String token = jwtUtil.generateToken(member.getEmail());

            log.info("Google OAuth başarılı: {}, Yeni kullanıcı: {}", email, isNewUser);

            // GoogleAuthResponse'u doğru yapıcı metot ile oluştur.
            // isNewUser bilgisini, mesaj içinde döndürmek daha mantıklı olabilir.
            String message = isNewUser ? "Yeni kullanıcı olarak başarıyla giriş yapıldı." : "Başarıyla giriş yapıldı.";
            return new GoogleAuthResponse(token, member.getMemberId(), message, true);

        } catch (Exception e) {
            log.error("Google OAuth sırasında hata: {}", e.getMessage(), e);
            throw new RuntimeException("Google ile giriş yapılamadı: " + e.getMessage());
        }
    }
}