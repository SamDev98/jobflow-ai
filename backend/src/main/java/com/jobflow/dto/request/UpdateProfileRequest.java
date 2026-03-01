package com.jobflow.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.util.List;

public record UpdateProfileRequest(
        @Min(0) @Max(50) Integer yearsExperience,
        List<String> techStack,
        String location,
        String workMode,
        @Min(0) Integer salaryMinUsd
) {}
