package com.muftisaleh.bayah;

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
@Table(name = "bayah_request")
@Getter
@Setter
@NoArgsConstructor
public class BayahRequest extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String phone;
    private String district;

    @Column(columnDefinition = "text")
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private BayahStatus status = BayahStatus.NEW;

    @Column(name = "admin_note", columnDefinition = "text")
    private String adminNote;
}
