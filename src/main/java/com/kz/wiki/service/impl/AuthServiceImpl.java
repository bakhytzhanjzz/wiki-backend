package com.kz.wiki.service.impl;

import com.kz.wiki.dto.request.LoginRequest;
import com.kz.wiki.dto.request.RegisterRequest;
import com.kz.wiki.dto.response.AuthResponse;
import com.kz.wiki.entity.Company;
import com.kz.wiki.entity.User;
import com.kz.wiki.exception.BadRequestException;
import com.kz.wiki.exception.UnauthorizedException;
import com.kz.wiki.repository.CompanyRepository;
import com.kz.wiki.repository.UserRepository;
import com.kz.wiki.security.CustomUserDetails;
import com.kz.wiki.security.JwtTokenProvider;
import com.kz.wiki.service.AuthService;
import com.kz.wiki.util.TenantIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Generate tenant ID from company name
        String tenantId = TenantIdGenerator.generateFromCompanyName(request.getCompanyName());

        // Check if tenant ID already exists (unlikely but possible)
        if (companyRepository.existsByTenantId(tenantId)) {
            tenantId = TenantIdGenerator.generateFromCompanyName(request.getCompanyName() + "-" + System.currentTimeMillis());
        }

        // Create company
        Company company = new Company();
        company.setTenantId(tenantId);
        company.setName(request.getCompanyName());
        company.setOwnerEmail(request.getEmail());
        company.setCreatedAt(LocalDateTime.now());
        company = companyRepository.save(company);

        // Check if user already exists in this tenant
        if (userRepository.existsByEmailAndTenantId(request.getEmail(), tenantId)) {
            throw new BadRequestException("User with email " + request.getEmail() + " already exists");
        }

        // Create user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(request.getRole());
        user.setTenantId(tenantId);
        user.setCreatedAt(LocalDateTime.now());
        user = userRepository.save(user);

        // Generate tokens
        UserDetails userDetails = new CustomUserDetails(user);
        String accessToken = jwtTokenProvider.generateToken(userDetails, tenantId);
        String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails, tenantId);

        log.info("User registered: {} for tenant: {}", request.getEmail(), tenantId);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .role(user.getRole().name())
                        .tenantId(tenantId)
                        .build())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

            String tenantId = user.getTenantId();
            String accessToken = jwtTokenProvider.generateToken(userDetails, tenantId);
            String refreshToken = jwtTokenProvider.generateRefreshToken(userDetails, tenantId);

            log.info("User logged in: {} for tenant: {}", request.getEmail(), tenantId);

            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .user(AuthResponse.UserInfo.builder()
                            .id(user.getId())
                            .email(user.getEmail())
                            .fullName(user.getFullName())
                            .role(user.getRole().name())
                            .tenantId(tenantId)
                            .build())
                    .build();
        } catch (org.springframework.security.authentication.BadCredentialsException e) {
            throw new UnauthorizedException("Invalid email or password");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(String refreshToken) {
        try {
            String username = jwtTokenProvider.extractUsername(refreshToken);
            String tenantId = jwtTokenProvider.extractTenantId(refreshToken);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            
            if (jwtTokenProvider.validateToken(refreshToken, userDetails)) {
                String newAccessToken = jwtTokenProvider.generateToken(userDetails, tenantId);
                String newRefreshToken = jwtTokenProvider.generateRefreshToken(userDetails, tenantId);

                User user = userRepository.findByEmail(username)
                        .orElseThrow(() -> new UnauthorizedException("User not found"));

                return AuthResponse.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(newRefreshToken)
                        .user(AuthResponse.UserInfo.builder()
                                .id(user.getId())
                                .email(user.getEmail())
                                .fullName(user.getFullName())
                                .role(user.getRole().name())
                                .tenantId(tenantId)
                                .build())
                        .build();
            } else {
                throw new UnauthorizedException("Invalid refresh token");
            }
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid refresh token");
        }
    }
}



