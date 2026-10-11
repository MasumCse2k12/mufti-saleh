package com.muftisaleh.masail;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public final class MasailDtos {

    private MasailDtos() {
    }

    public record MasailResponse(
            UUID id,
            String question,
            String answer,
            String category,
            Instant createdAt) {
    }

    public record CreateRequest(
            @NotBlank String question,
            @NotBlank String answer,
            String category,
            boolean published) {
    }

    public record UpdateRequest(
            String question,
            String answer,
            String category,
            Boolean published) {
    }
}
