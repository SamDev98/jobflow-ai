package com.jobflow.controller;

import com.jobflow.dto.request.GenerateInterviewPrepRequest;
import com.jobflow.dto.response.InterviewPrepResponse;
import com.jobflow.service.InterviewPrepService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/interview-prep")
@RequiredArgsConstructor
@Tag(name = "Interview Prep", description = "AI-generated interview questions")
public class InterviewPrepController {

    private final InterviewPrepService interviewPrepService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Generate interview questions for a job role")
    public InterviewPrepResponse generate(@Valid @RequestBody GenerateInterviewPrepRequest request)
            throws Exception {
        return interviewPrepService.generate(request);
    }

    @GetMapping
    @Operation(summary = "List all interview preps for current user")
    public List<InterviewPrepResponse> list() {
        return interviewPrepService.listForCurrentUser();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a specific interview prep by ID")
    public InterviewPrepResponse getById(@PathVariable UUID id) {
        return interviewPrepService.getById(id);
    }
}
