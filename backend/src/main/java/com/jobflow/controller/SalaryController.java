package com.jobflow.controller;

import com.jobflow.dto.response.SalaryRangeResponse;
import com.jobflow.service.SalaryResearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/salary")
@RequiredArgsConstructor
@Tag(name = "Salary", description = "AI-powered salary research")
public class SalaryController {

    private final SalaryResearchService salaryResearchService;

    @PostMapping("/research")
    @Operation(summary = "Research salary range for a role")
    public SalaryRangeResponse research(
            @RequestParam @NotBlank String jobTitle,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) UUID applicationId) throws Exception {
        return salaryResearchService.research(jobTitle, company, location, applicationId);
    }
}
