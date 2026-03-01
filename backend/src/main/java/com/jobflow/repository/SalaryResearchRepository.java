package com.jobflow.repository;

import com.jobflow.entity.SalaryResearch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SalaryResearchRepository extends JpaRepository<SalaryResearch, UUID> {

    List<SalaryResearch> findByUserIdOrderByCreatedAtDesc(UUID userId);
}
