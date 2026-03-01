package com.jobflow.entity;

import com.jobflow.entity.enums.Stage;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "applications", indexes = {
        @Index(name = "idx_applications_user_stage", columnList = "user_id, stage"),
        @Index(name = "idx_applications_deadline", columnList = "deadline")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String company;

    @Column(nullable = false)
    private String role;

    @Column(name = "job_description", columnDefinition = "TEXT")
    private String jobDescription;

    @Column(name = "jd_url")
    private String jdUrl;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "tech_stack", columnDefinition = "text[]")
    private List<String> techStack;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Stage stage = Stage.APPLIED;

    @Column(name = "salary_range_low_usd")
    private Integer salaryRangeLowUsd;

    @Column(name = "salary_range_high_usd")
    private Integer salaryRangeHighUsd;

    private LocalDate deadline;

    @Column(name = "last_recruiter_contact")
    private Instant lastRecruiterContact;

    @Column(name = "awaiting_response")
    @Builder.Default
    private Boolean awaitingResponse = false;

    @Column(name = "interview_datetime")
    private Instant interviewDatetime;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    @Builder.Default
    private Instant updatedAt = Instant.now();

    // Soft delete
    @Column(name = "deleted_at")
    private Instant deletedAt;

    @PreUpdate
    void onUpdate() {
        this.updatedAt = Instant.now();
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}
