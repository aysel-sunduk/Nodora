package com.example.demo.config;

import com.example.demo.model.members.Member;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Uygulama başlangıcında gerekli test verilerini oluşturan sınıf.
 * Bu sınıf Spring Boot uygulaması başladığında otomatik olarak çalışır.
 */
@Component
@RequiredArgsConstructor
@Order(1)
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Admin kullanıcısı oluştur (yoksa)
        createAdminUserIfNotExists();
        
        // Test kullanıcısı oluştur (yoksa)
        createTestUserIfNotExists();
        
        // Mevcut admin kullanıcısının şifresini hashle (eski kod)
        updateExistingAdminPassword();
    }
    
    /**
     * Admin kullanıcısı yoksa oluşturur
     */
    private void createAdminUserIfNotExists() {
        if (!memberRepository.existsByEmail("admin@example.com")) {
            Member adminUser = new Member();
            adminUser.setName("Admin");
            adminUser.setSurname("User");
            adminUser.setEmail("admin@example.com");
            adminUser.setMemberName("admin");
            adminUser.setPassword(passwordEncoder.encode("admin123"));
            adminUser.setIsAdmin(true);
            adminUser.setMembersActive(true);
            
            memberRepository.save(adminUser);
            System.out.println("✅ Admin kullanıcısı oluşturuldu: admin@example.com / admin123");
        }
    }
    
    /**
     * Test kullanıcısı yoksa oluşturur
     */
    private void createTestUserIfNotExists() {
        if (!memberRepository.existsByEmail("test@example.com")) {
            Member testUser = new Member();
            testUser.setName("Test");
            testUser.setSurname("Kullanıcı");
            testUser.setEmail("test@example.com");
            testUser.setMemberName("testuser");
            testUser.setPassword(passwordEncoder.encode("test123"));
            testUser.setIsAdmin(false);
            testUser.setMembersActive(true);
            
            memberRepository.save(testUser);
            System.out.println("✅ Test kullanıcısı oluşturuldu: test@example.com / test123");
        }
    }
    
    /**
     * Mevcut admin kullanıcısının şifresini günceller (eski kod - backward compatibility)
     */
    private void updateExistingAdminPassword() {
        Member adminUser = memberRepository.findByEmail("admin@example.com").orElse(null);
        if (adminUser != null && adminUser.getPassword().equals("123456")) {
            adminUser.setPassword(passwordEncoder.encode("123456"));
            memberRepository.save(adminUser);
            System.out.println("🔄 Admin kullanıcısının şifresi hashlenmiş!");
        }
    }
} 