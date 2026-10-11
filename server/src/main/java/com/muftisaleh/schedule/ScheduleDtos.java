package com.muftisaleh.schedule;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class ScheduleDtos {

    private ScheduleDtos() {
    }

    public record ScheduleEventResponse(
            UUID id,
            String title,
            String description,
            Instant eventAt,
            String location,
            EventType type) {
    }

    public record CreateRequest(
            @NotBlank String title,
            String description,
            @NotNull Instant eventAt,
            String location,
            EventType type) {
    }

    public record UpdateRequest(
            String title,
            String description,
            Instant eventAt,
            String location,
            EventType type) {
    }
}
