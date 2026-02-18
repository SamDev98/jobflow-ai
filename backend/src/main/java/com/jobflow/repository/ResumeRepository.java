package com.jobflow.repository;

import com.jobflow.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ResumeRepository extends JpaRepository<Resume, UUID> {

    List<Resume> findByUserIdOrderByCreatedAtDesc(UUID userId);

    List<Resume> findByApplicationIdOrderByCreatedAtDesc(UUID applicationId);
}
