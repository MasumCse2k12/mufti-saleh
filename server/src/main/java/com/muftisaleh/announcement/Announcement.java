package com.muftisaleh.announcement;

import java.time.Instant;
import java.util.UUID;

import com.muftisaleh.common.jpa.Auditable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "announcement")
@Getter
@Setter
@NoArgsConstructor
public class Announcement extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "text")
    private String body;

    /** Whether this is the top pinned notice — only one should be pinned at a time. */
    @Column(nullable = false)
    private boolean pinned = false;

    /** Null means it never expires. */
    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "published_at", nullable = false)
    private Instant publishedAt;
}
