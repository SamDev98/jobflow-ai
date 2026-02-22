package com.jobflow.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobflow.dto.request.CreateHistoryRequest;
import com.jobflow.dto.response.SalaryRangeResponse;
import com.jobflow.entity.SalaryResearch;
import com.jobflow.entity.User;
import com.jobflow.entity.enums.HistoryType;
import com.jobflow.repository.SalaryResearchRepository;
import com.jobflow.util.PromptTemplates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class SalaryResearchService {

        private final LLMOrchestrator llm;
        private final SalaryResearchRepository salaryResearchRepository;
        private final UserService userService;
        private final ObjectMapper objectMapper;
        private final HistoryService historyService;

        @Transactional
        public SalaryRangeResponse research(String jobTitle, String company,
                        String location, UUID applicationId) throws JsonProcessingException {
                User user = userService.getCurrentUser();
                var profile = user.getProfile();

                int yearsExp = profile != null ? profile.getYearsExperience() : 0;
                String userLocation = (profile != null && profile.getLocation() != null)
                                ? profile.getLocation()
                                : location;

                // In production, add Playwright scraping of Levels.fyi / Glassdoor here
                String collectedData = "Job title: %s at %s, Location: %s, YoE: %d"
                                .formatted(jobTitle, company, location, yearsExp);

                String prompt = PromptTemplates.SALARY_RESEARCH.formatted(collectedData, yearsExp, userLocation);
                Map<String, Object> result = objectMapper.convertValue(
                                llm.completeAsJson(prompt, user.getTier(), Map.class),
                                new TypeReference<Map<String, Object>>() {
                                });

                Map<String, Object> rangeUsd = result.get("range_usd") == null
                                ? Map.of()
                                : objectMapper.convertValue(
                                                result.get("range_usd"),
                                                new TypeReference<Map<String, Object>>() {
                                                });
                int low = toInt(rangeUsd.get("low"));
                int mid = toInt(rangeUsd.get("mid"));
                int high = toInt(rangeUsd.get("high"));
                String reasoning = result.get("reasoning") instanceof String value ? value : "";
                int confidence = result.containsKey("confidence") ? toInt(result.get("confidence")) : 5;

                SalaryResearch entity = SalaryResearch.builder()
                                .user(user)
                                .jobTitle(jobTitle)
                                .company(company)
                                .location(location)
                                .rangeLowUsd(low)
                                .rangeMidUsd(mid)
                                .rangeHighUsd(high)
                                .reasoning(reasoning)
                                .confidenceScore(confidence)
                                .dataSources(objectMapper.writeValueAsString(Map.of("raw", collectedData)))
                                .build();
                entity = salaryResearchRepository.save(entity);

                // Save to history
                try {
                        String title = "Salary Research: " + jobTitle + (company != null ? " at " + company : "");
                        historyService.createHistory(CreateHistoryRequest.builder()
                                        .type(HistoryType.SALARY_RESEARCH)
                                        .title(title)
                                        .content("Research for " + jobTitle + " in " + location + ". Range: $" + low
                                                        + " - $"
                                                        + high)
                                        .metadata(objectMapper.writeValueAsString(Map.of(
                                                        "low", low,
                                                        "high", high,
                                                        "reasoning", reasoning)))
                                        .build());
                } catch (Exception e) {
                        log.warn("Failed to save salary research to history", e);
                }

                return new SalaryRangeResponse(entity.getId(), low, mid, high, reasoning, confidence);
        }

        private int toInt(Object value) {
                if (value instanceof Number n)
                        return n.intValue();
                return 0;
        }
}
