package com.muftisaleh.admin;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public final class AdminDtos {

    private AdminDtos() {
    }

    public record LoginRequest(
            @NotBlank String username,
            @NotBlank String password) {
    }

    public record LoginResponse(
            String accessToken,
            long expiresInSeconds,
            AdminDto admin) {
    }

    public record AdminDto(
            UUID id,
            String username,
            String displayName,
            AdminRole role,
            boolean active,
            boolean mustChangePassword,
            Instant lastLoginAt) {
    }

    public record ChangePasswordRequest(
            @NotBlank String currentPassword,
            @NotBlank String newPassword) {
    }

    public record CreateAdminRequest(
            @NotBlank String username,
            @NotBlank String displayName,
            @NotBlank String password,
            AdminRole role) {
    }
}
