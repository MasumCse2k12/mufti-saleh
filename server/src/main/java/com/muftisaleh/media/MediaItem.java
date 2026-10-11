package com.muftisaleh.media;

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
@Table(name = "media_item")
@Getter
@Setter
@NoArgsConstructor
public class MediaItem extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private MediaType type = MediaType.BAYAAN;

    /** YouTube video ID (11 chars), e.g. "dQw4w9WgXcQ" */
    @Column(name = "youtube_id", length = 20)
    private String youtubeId;

    /** Optional direct audio/video URL for self-hosted content */
    @Column(name = "media_url")
    private String mediaUrl;

    @Column(name = "duration_seconds")
    private Integer durationSeconds;

    @Column(name = "published_at", nullable = false)
    private Instant publishedAt;
}
