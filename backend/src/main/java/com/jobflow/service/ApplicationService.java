package com.jobflow.service;

import com.jobflow.dto.request.CreateApplicationRequest;
import com.jobflow.dto.request.UpdateApplicationRequest;
import com.jobflow.dto.response.ApplicationResponse;
import com.jobflow.entity.JobApplication;
import com.jobflow.entity.User;
import com.jobflow.entity.enums.Stage;
import com.jobflow.exception.ResourceNotFoundException;
import com.jobflow.repository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ApplicationService {

    private static final String APPLICATION_RESOURCE = "Application";

    private final JobApplicationRepository applicationRepository;
    private final UserService userService;

    @Transactional(readOnly = true)
    public Page<ApplicationResponse> listApplications(Stage stage, Pageable pageable) {
        User user = userService.getCurrentUser();
        Page<JobApplication> page = stage != null
                ? applicationRepository.findByUserIdAndStageAndDeletedAtIsNull(user.getId(), stage, pageable)
                : applicationRepository.findByUserIdAndDeletedAtIsNull(user.getId(), pageable);
        return page.map(ApplicationResponse::from);
    }

    @Transactional(readOnly = true)
    public ApplicationResponse getById(UUID id) {
        User user = userService.getCurrentUser();
        JobApplication app = applicationRepository
                .findByIdAndUserIdAndDeletedAtIsNull(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(APPLICATION_RESOURCE, id));
        return ApplicationResponse.from(app);
    }

    @Transactional
    public ApplicationResponse create(CreateApplicationRequest request) {
        User user = userService.getCurrentUser();
        JobApplication app = JobApplication.builder()
                .user(user)
                .company(request.company())
                .role(request.role())
                .jobDescription(request.jobDescription())
                .jdUrl(request.jdUrl())
                .techStack(request.techStack())
                .stage(request.stage() != null ? request.stage() : Stage.APPLIED)
                .salaryRangeLowUsd(request.salaryRangeLowUsd())
                .salaryRangeHighUsd(request.salaryRangeHighUsd())
                .deadline(request.deadline())
                .notes(request.notes())
                .build();
        return ApplicationResponse.from(applicationRepository.save(app));
    }

    @Transactional
    public ApplicationResponse update(UUID id, UpdateApplicationRequest request) {
        User user = userService.getCurrentUser();
        JobApplication app = applicationRepository
                .findByIdAndUserIdAndDeletedAtIsNull(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(APPLICATION_RESOURCE, id));

        if (request.company() != null)
            app.setCompany(request.company());
        if (request.role() != null)
            app.setRole(request.role());
        if (request.jobDescription() != null)
            app.setJobDescription(request.jobDescription());
        if (request.jdUrl() != null)
            app.setJdUrl(request.jdUrl());
        if (request.techStack() != null)
            app.setTechStack(request.techStack());
        if (request.stage() != null)
            app.setStage(request.stage());
        if (request.salaryRangeLowUsd() != null)
            app.setSalaryRangeLowUsd(request.salaryRangeLowUsd());
        if (request.salaryRangeHighUsd() != null)
            app.setSalaryRangeHighUsd(request.salaryRangeHighUsd());
        if (request.deadline() != null)
            app.setDeadline(request.deadline());
        if (request.awaitingResponse() != null)
            app.setAwaitingResponse(request.awaitingResponse());
        if (request.notes() != null)
            app.setNotes(request.notes());

        return ApplicationResponse.from(applicationRepository.save(app));
    }

    @Transactional
    public void delete(UUID id) {
        User user = userService.getCurrentUser();
        JobApplication app = applicationRepository
                .findByIdAndUserIdAndDeletedAtIsNull(id, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(APPLICATION_RESOURCE, id));
        app.setDeletedAt(Instant.now());
        applicationRepository.save(app);
    }
}
