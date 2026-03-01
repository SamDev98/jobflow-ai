package com.jobflow.repository;

import com.jobflow.entity.InterviewPrep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InterviewPrepRepository extends JpaRepository<InterviewPrep, UUID> {

    List<InterviewPrep> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<InterviewPrep> findByApplicationIdOrderByCreatedAtDesc(UUID applicationId);
}
