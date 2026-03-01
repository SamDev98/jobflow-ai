package com.jobflow.dto.response;

import com.jobflow.entity.JobApplication;
import com.jobflow.entity.enums.Stage;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record ApplicationResponse(
        UUID id,
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
        Instant interviewDatetime,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {
    public static ApplicationResponse from(JobApplication app) {
        return new ApplicationResponse(
                app.getId(),
                app.getCompany(),
                app.getRole(),
                app.getJobDescription(),
                app.getJdUrl(),
                app.getTechStack(),
                app.getStage(),
                app.getSalaryRangeLowUsd(),
                app.getSalaryRangeHighUsd(),
                app.getDeadline(),
                app.getAwaitingResponse(),
                app.getInterviewDatetime(),
                app.getNotes(),
                app.getCreatedAt(),
                app.getUpdatedAt()
        );
    }
}
