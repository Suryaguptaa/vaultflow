package com.finance.vaultflow.service;

import com.finance.vaultflow.dto.*;
import com.finance.vaultflow.enums.Role;
import com.finance.vaultflow.enums.UserStatus;
import com.finance.vaultflow.exception.AppException;
import com.finance.vaultflow.model.User;
import com.finance.vaultflow.repository.UserRepository;
import com.finance.vaultflow.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException("Email already in use", HttpStatus.CONFLICT);
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.VIEWER)
                .status(UserStatus.ACTIVE)
                .build();

        userRepository.save(user);

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .expiresIn(86400000)
                .id(user.getId().toString())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));

        if (user.getStatus() == com.finance.vaultflow.enums.UserStatus.INACTIVE) {
            throw new AppException("Account is inactive", HttpStatus.FORBIDDEN);
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        return AuthResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .expiresIn(86400000)
                .id(user.getId().toString())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}