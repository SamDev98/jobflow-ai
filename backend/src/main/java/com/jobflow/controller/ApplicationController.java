package com.jobflow.controller;

import com.jobflow.dto.request.CreateApplicationRequest;
import com.jobflow.dto.request.UpdateApplicationRequest;
import com.jobflow.dto.response.ApplicationResponse;
import com.jobflow.entity.enums.Stage;
import com.jobflow.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
@Tag(name = "Applications", description = "Job application CRUD")
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping
    @Operation(summary = "List applications (paginated, optional stage filter)")
    public Page<ApplicationResponse> list(
            @RequestParam(required = false) Stage stage,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return applicationService.listApplications(stage, pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get application by ID")
    public ApplicationResponse getById(@PathVariable UUID id) {
        return applicationService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new application")
    public ApplicationResponse create(@Valid @RequestBody CreateApplicationRequest request) {
        return applicationService.create(request);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update an application (including stage change)")
    public ApplicationResponse update(@PathVariable UUID id,
                                       @Valid @RequestBody UpdateApplicationRequest request) {
        return applicationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Soft-delete an application")
    public void delete(@PathVariable UUID id) {
        applicationService.delete(id);
    }
}
