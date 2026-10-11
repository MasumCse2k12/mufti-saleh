package com.muftisaleh.schedule;

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
@Table(name = "schedule_event")
@Getter
@Setter
@NoArgsConstructor
public class ScheduleEvent extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "event_at", nullable = false)
    private Instant eventAt;

    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private EventType type = EventType.PROGRAM;
}
