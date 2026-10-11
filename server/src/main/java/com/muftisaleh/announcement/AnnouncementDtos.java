package com.muftisaleh.announcement;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class AnnouncementDtos {

    private AnnouncementDtos() {
    }

    public record AnnouncementResponse(
            UUID id,
            String title,
            String body,
            boolean pinned,
            Instant publishedAt,
            Instant expiresAt) {
    }

    public record CreateRequest(
            @NotBlank String title,
            @NotBlank String body,
            boolean pinned,
            @NotNull Instant publishedAt,
            Instant expiresAt) {
    }

    public record UpdateRequest(
            String title,
            String body,
            Boolean pinned,
            Instant publishedAt,
            Instant expiresAt) {
    }
}
