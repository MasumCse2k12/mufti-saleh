package com.muftisaleh.bayah;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;

public final class BayahDtos {

    private BayahDtos() {
    }

    public record BayahResponse(
            UUID id,
            String name,
            String phone,
            String district,
            String note,
            BayahStatus status,
            String adminNote,
            Instant createdAt) {
    }

    public record SubmitRequest(
            @NotBlank String name,
            String phone,
            String district,
            String note) {
    }

    public record UpdateStatusRequest(
            BayahStatus status,
            String adminNote) {
    }
}
