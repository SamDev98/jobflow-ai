package com.jobflow.dto.request;

import com.jobflow.entity.enums.Stage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record CreateApplicationRequest(
        @NotBlank String company,
        @NotBlank String role,
        String jobDescription,
        String jdUrl,
        List<String> techStack,
        Stage stage,
        Integer salaryRangeLowUsd,
        Integer salaryRangeHighUsd,
        LocalDate deadline,
        @Size(max = 5000) String notes
) {}
