package com.jobflow.controller;

import com.jobflow.dto.response.OptimizedResumeResponse;
import com.jobflow.service.ResumeOptimizerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/resumes")
@RequiredArgsConstructor
@Tag(name = "Resumes", description = "AI-powered resume optimization")
public class ResumeController {

    private final ResumeOptimizerService resumeOptimizerService;

    @PostMapping(value = "/optimize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a .docx resume, a style template, and optimize it for a job description")
    public OptimizedResumeResponse optimize(
            @RequestPart("resume") MultipartFile resumeFile,
            @RequestPart("template") MultipartFile templateFile,
            @RequestPart("jd") String jobDescription,
            @RequestParam(required = false) UUID applicationId) throws Exception {
        return resumeOptimizerService.optimize(resumeFile, templateFile, jobDescription, applicationId);
    }
}
