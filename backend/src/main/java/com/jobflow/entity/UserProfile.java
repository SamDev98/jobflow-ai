package com.jobflow.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile {

    @Id
    private UUID userId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "years_experience")
    private Integer yearsExperience;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "tech_stack", columnDefinition = "text[]")
    private List<String> techStack;

    private String location;

    @Column(name = "salary_min_usd")
    private Integer salaryMinUsd;

    @Column(name = "work_mode")
    private String workMode;

    @Column(name = "base_resume_s3_key")
    private String baseResumeS3Key;

    @Column(name = "updated_at")
    @Builder.Default
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
