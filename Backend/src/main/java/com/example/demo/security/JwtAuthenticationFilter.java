// src/main/java/com/example/demo/security/JwtAuthenticationFilter.java
package com.example.demo.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService; // <<< GÜNCELLENMİŞ: CustomUserDetailsService buraya enjekte edilir.

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String requestPath = request.getRequestURI();
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            if (jwtUtil.validateToken(token)) {
                Claims claims = jwtUtil.extractAllClaims(token);
                String email = claims.getSubject();

                // SecurityContext'te zaten bir Authentication nesnesi varsa işlem yapma.
                if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                    // UserDetailsService ile UserDetails nesnesini yükle.
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                    // UserDetails nesnesi ile Authentication nesnesi oluştur.
                    // Authorities bilgisi CustomPermissionEvaluator için önemli değil,
                    // ancak `hasRole` gibi diğer kontroller için gereklidir.
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }

        } catch (ExpiredJwtException | MalformedJwtException e) {
            if (isPublicEndpoint(requestPath)) {
                System.out.println("⚠️ JWT Token hatası (public endpoint için önemli değil): " + requestPath + " - " + e.getMessage());
                chain.doFilter(request, response);
                return;
            } else {
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED, "Geçersiz token");
                return;
            }
        } catch (Exception e) {
            if (isPublicEndpoint(requestPath)) {
                System.out.println("⚠️ JWT işleme hatası (public endpoint için önemli değil): " + requestPath + " - " + e.getMessage());
                chain.doFilter(request, response);
                return;
            } else {
                ((HttpServletResponse) response).sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Authentication failed");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(String requestPath) {
        return requestPath.startsWith("/api/auth/") ||
                requestPath.startsWith("/swagger-ui") ||
                requestPath.startsWith("/v3/api-docs") ||
                requestPath.startsWith("/swagger-resources") ||
                requestPath.startsWith("/webjars") ||
                requestPath.equals("/swagger-ui.html");
    }
}