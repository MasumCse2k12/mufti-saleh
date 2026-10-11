package com.muftisaleh.media;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class MediaDtos {

    private MediaDtos() {
    }

    public record MediaResponse(
            UUID id,
            String title,
            String description,
            MediaType type,
            String youtubeId,
            String mediaUrl,
            Integer durationSeconds,
            Instant publishedAt) {
    }

    public record CreateRequest(
            @NotBlank String title,
            String description,
            MediaType type,
            String youtubeId,
            String mediaUrl,
            Integer durationSeconds,
            @NotNull Instant publishedAt) {
    }

    public record UpdateRequest(
            String title,
            String description,
            MediaType type,
            String youtubeId,
            String mediaUrl,
            Integer durationSeconds,
            Instant publishedAt) {
    }
}
