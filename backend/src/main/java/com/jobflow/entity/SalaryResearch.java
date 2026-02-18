package com.jobflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "salary_researches")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryResearch {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private JobApplication application;

    @Column(name = "job_title", nullable = false)
    private String jobTitle;

    private String company;
    private String location;

    @Column(name = "range_low_usd", nullable = false)
    private Integer rangeLowUsd;

    @Column(name = "range_mid_usd", nullable = false)
    private Integer rangeMidUsd;

    @Column(name = "range_high_usd", nullable = false)
    private Integer rangeHighUsd;

    // JSON stored as text
    @Column(name = "data_sources", columnDefinition = "TEXT")
    private String dataSources;

    @Column(columnDefinition = "TEXT")
    private String reasoning;

    @Column(name = "confidence_score")
    private Integer confidenceScore;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
