package com.jobflow.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;
import java.util.UUID;

public record GenerateInterviewPrepRequest(
        UUID applicationId,
        @NotBlank String jobTitle,
        String company,
        String jobDescription,
        List<String> techStack
) {}
