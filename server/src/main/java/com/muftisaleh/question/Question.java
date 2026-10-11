package com.muftisaleh.question;

import java.time.Instant;
import java.util.UUID;

import com.muftisaleh.common.jpa.Auditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "question")
@Getter
@Setter
@NoArgsConstructor
public class Question extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, columnDefinition = "text")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private QuestionCategory category = QuestionCategory.GENERAL;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private QuestionStatus status = QuestionStatus.PENDING;

    @Column(columnDefinition = "text")
    private String answer;

    @Column(name = "answered_at")
    private Instant answeredAt;

    /** Optional: name submitted by the questioner. */
    @Column(name = "submitter_name")
    private String submitterName;

    /** Optional: phone or contact. Never exposed publicly. */
    @Column(name = "submitter_contact")
    private String submitterContact;
}
