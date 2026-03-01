package com.jobflow.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "resumes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Resume {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private JobApplication application;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "s3_key_docx", nullable = false)
    private String s3KeyDocx;

    @Column(name = "s3_key_pdf", nullable = false)
    private String s3KeyPdf;

    @Column(name = "ats_score")
    private Integer atsScore;

    // JSON stored as text — parsed in service layer
    @Column(name = "changes_made", columnDefinition = "TEXT")
    private String changesMade;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();
}
