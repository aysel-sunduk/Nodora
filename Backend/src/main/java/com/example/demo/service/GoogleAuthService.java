package com.example.demo.service;

import com.example.demo.dto.request.GoogleAuthRequest;
import com.example.demo.dto.response.GoogleAuthResponse;

public interface GoogleAuthService {
    
    /**
     * Google OAuth ile giriş/kayıt işlemi
     */
    GoogleAuthResponse authenticateWithGoogle(GoogleAuthRequest request);
} 