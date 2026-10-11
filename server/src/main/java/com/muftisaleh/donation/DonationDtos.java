package com.muftisaleh.donation;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public final class DonationDtos {

    private DonationDtos() {
    }

    public record DonationResponse(
            UUID id,
            String donorName,
            BigDecimal amount,
            DonationPurpose purpose,
            String note,
            Instant createdAt) {
    }

    public record DonationSummary(
            Map<String, BigDecimal> byPurpose,
            BigDecimal total) {
    }

    public record RecordRequest(
            String donorName,
            @NotNull @Positive BigDecimal amount,
            @NotNull DonationPurpose purpose,
            String note) {
    }
}
