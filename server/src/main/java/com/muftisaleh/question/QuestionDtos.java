package com.muftisaleh.question;

import java.time.Instant;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class QuestionDtos {

    private QuestionDtos() {
    }

    /** What the public sees — no submitter info. */
    public record QuestionPublicResponse(
            UUID id,
            String body,
            QuestionCategory category,
            String answer,
            Instant answeredAt) {
    }

    /** What the admin sees — includes submitter info and status. */
    public record QuestionAdminResponse(
            UUID id,
            String body,
            QuestionCategory category,
            QuestionStatus status,
            String answer,
            Instant answeredAt,
            String submitterName,
            String submitterContact,
            Instant createdAt) {
    }

    public record SubmitRequest(
            @NotBlank @Size(max = 2000) String body,
            QuestionCategory category,
            String submitterName,
            String submitterContact) {
    }

    public record AnswerRequest(@NotBlank String answer) {
    }
}
