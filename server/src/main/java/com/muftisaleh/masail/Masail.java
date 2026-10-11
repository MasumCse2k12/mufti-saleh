package com.muftisaleh.masail;

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

/**
 * Women's special masail (মহিলাদের বিশেষ মাসায়েল).
 */
@Entity
@Table(name = "masail")
@Getter
@Setter
@NoArgsConstructor
public class Masail extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, columnDefinition = "text")
    private String question;

    @Column(nullable = false, columnDefinition = "text")
    private String answer;

    /** Category for grouping: TAHARA, SALAT, SAWM, NIKAH, MISC */
    @Column(nullable = false, length = 32)
    private String category = "MISC";

    @Column(nullable = false)
    private boolean published = true;
}
