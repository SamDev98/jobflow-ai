package com.jobflow.dto.request;

import com.jobflow.entity.enums.Stage;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.List;

public record UpdateApplicationRequest(
        String company,
        String role,
        String jobDescription,
        String jdUrl,
        List<String> techStack,
        Stage stage,
        Integer salaryRangeLowUsd,
        Integer salaryRangeHighUsd,
        LocalDate deadline,
        Boolean awaitingResponse,
        @Size(max = 5000) String notes
) {}
