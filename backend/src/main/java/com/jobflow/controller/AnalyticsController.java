package com.jobflow.controller;

import com.jobflow.entity.enums.Stage;
import com.jobflow.repository.JobApplicationRepository;
import com.jobflow.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics", description = "Application analytics")
public class AnalyticsController {

    private final JobApplicationRepository applicationRepository;
    private final UserService userService;

    @GetMapping("/by-stage")
    @Operation(summary = "Count applications grouped by stage for the current user")
    public Map<String, Long> byStage() {
        var user = userService.getCurrentUser();

        // Initialize all stages to 0
        Map<String, Long> counts = Arrays.stream(Stage.values())
                .collect(Collectors.toMap(Stage::name, s -> 0L));

        // Override with actual counts
        applicationRepository.countByStageForUser(user.getId())
                .forEach(row -> counts.put(row[0].toString(), (Long) row[1]));

        return counts;
    }
}
