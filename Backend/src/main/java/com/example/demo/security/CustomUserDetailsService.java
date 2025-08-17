// src/main/java/com/example/demo/security/CustomUserDetailsService.java

package com.example.demo.security;

import com.example.demo.model.members.Member;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // E-postaya göre kullanıcıyı veritabanından bul
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Kullanıcı bulunamadı: " + email));

        // KULLANICININ YETKİLERİNİ (ROLLERİNİ) BELİRLE VE EKLE
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Eğer kullanıcı admin ise, ROLE_ADMIN yetkisini ekle
        if (Boolean.TRUE.equals(member.getIsAdmin())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            // Admin değilse varsayılan bir rol ekleyebiliriz (örneğin ROLE_USER)
            // Bu, diğer yetki kontrolleri için önemlidir.
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        // Spring Security'nin User sınıfını kullanarak UserDetails nesnesi oluştur
        return new org.springframework.security.core.userdetails.User(
                member.getEmail(),      // Username
                member.getPassword(),   // Password (veritabanından çekilen şifre)
                authorities             // Yetkiler
        );
    }
}
