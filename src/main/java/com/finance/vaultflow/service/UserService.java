package com.finance.vaultflow.service;

import com.finance.vaultflow.dto.*;
import com.finance.vaultflow.exception.AppException;
import com.finance.vaultflow.model.User;
import com.finance.vaultflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<UserResponse> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return userRepository.findAll(pageable).map(this::toResponse);
    }

    public UserResponse getUserById(UUID id) {
        return toResponse(findUserOrThrow(id));
    }

    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException("Email already in use", HttpStatus.CONFLICT);
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .status(com.finance.vaultflow.enums.UserStatus.ACTIVE)
                .build();
        return toResponse(userRepository.save(user));
    }

    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        User user = findUserOrThrow(id);
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        return toResponse(userRepository.save(user));
    }

    public UserResponse updateRole(UUID id, UpdateRoleRequest request) {
        User user = findUserOrThrow(id);
        user.setRole(request.getRole());
        return toResponse(userRepository.save(user));
    }

    public UserResponse updateStatus(UUID id, UpdateStatusRequest request) {
        User user = findUserOrThrow(id);
        user.setStatus(request.getStatus());
        return toResponse(userRepository.save(user));
    }

    public void softDeleteUser(UUID id) {
        User user = findUserOrThrow(id);
        user.setDeletedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    private User findUserOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new AppException("User not found", HttpStatus.NOT_FOUND));
    }

    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}