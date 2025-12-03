package com.kz.wiki.service;

import com.kz.wiki.dto.request.LoginRequest;
import com.kz.wiki.dto.request.RegisterRequest;
import com.kz.wiki.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
}






