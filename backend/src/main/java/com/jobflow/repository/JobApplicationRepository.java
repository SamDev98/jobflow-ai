package com.jobflow.repository;

import com.jobflow.entity.JobApplication;
import com.jobflow.entity.enums.Stage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobApplicationRepository extends JpaRepository<JobApplication, UUID> {

    Page<JobApplication> findByUserIdAndDeletedAtIsNull(UUID userId, Pageable pageable);

    Page<JobApplication> findByUserIdAndStageAndDeletedAtIsNull(UUID userId, Stage stage, Pageable pageable);

    Optional<JobApplication> findByIdAndUserIdAndDeletedAtIsNull(UUID id, UUID userId);

    @Query("""
            SELECT a FROM JobApplication a
            WHERE a.user.id = :userId
            AND a.awaitingResponse = true
            AND a.deletedAt IS NULL
            AND a.updatedAt < :since
            """)
    List<JobApplication> findAwaitingResponse(@Param("userId") UUID userId,
                                               @Param("since") Instant since);

    @Query("""
            SELECT a FROM JobApplication a
            WHERE a.awaitingResponse = true
            AND a.deletedAt IS NULL
            AND a.updatedAt < :since
            """)
    List<JobApplication> findAllAwaitingResponse(@Param("since") Instant since);

    @Query("""
            SELECT a FROM JobApplication a
            WHERE a.deletedAt IS NULL
            AND a.deadline IS NOT NULL
            AND a.deadline <= :deadlineLimit
            """)
    List<JobApplication> findUpcomingDeadlines(@Param("deadlineLimit") java.time.LocalDate deadlineLimit);

    @Query("""
            SELECT a.stage, COUNT(a) FROM JobApplication a
            WHERE a.user.id = :userId AND a.deletedAt IS NULL
            GROUP BY a.stage
            """)
    List<Object[]> countByStageForUser(@Param("userId") UUID userId);
}
