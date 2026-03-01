package com.jobflow.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.jobflow.dto.request.CreateHistoryRequest;
import com.jobflow.dto.request.GenerateInterviewPrepRequest;
import com.jobflow.dto.response.InterviewPrepResponse;
import com.jobflow.entity.InterviewPrep;
import com.jobflow.entity.JobApplication;
import com.jobflow.entity.User;
import com.jobflow.entity.enums.HistoryType;
import com.jobflow.exception.ResourceNotFoundException;
import com.jobflow.repository.InterviewPrepRepository;
import com.jobflow.repository.JobApplicationRepository;
import com.jobflow.util.PromptTemplates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class InterviewPrepService {

        private final LLMOrchestrator llm;
        private final InterviewPrepRepository interviewPrepRepository;
        private final JobApplicationRepository jobApplicationRepository;
        private final UserService userService;
        private final ObjectMapper objectMapper;
        private final HistoryService historyService;

        @Transactional
        public InterviewPrepResponse generate(GenerateInterviewPrepRequest request) {
                User user = userService.getCurrentUser();

                JobApplication application = null;
                if (request.applicationId() != null) {
                        application = jobApplicationRepository
                                        .findByIdAndUserIdAndDeletedAtIsNull(request.applicationId(), user.getId())
                                        .orElseThrow(() -> new ResourceNotFoundException("Application",
                                                        request.applicationId()));
                }

                String company = request.company() != null ? request.company() : "the company";
                String jd = request.jobDescription() != null ? request.jobDescription() : "(no description provided)";
                String techStack = request.techStack() != null
                                ? String.join(", ", request.techStack())
                                : "(not specified)";

                String prompt = PromptTemplates.INTERVIEW_PREP.formatted(
                                request.jobTitle(), company, jd, techStack);

                List<?> rawList = llm.completeAsJson(prompt, user.getTier(), List.class);
                String questionsJson;
                try {
                        questionsJson = objectMapper.writeValueAsString(rawList);
                } catch (JsonProcessingException e) {
                        throw new IllegalStateException("Failed to serialize interview questions", e);
                }

                InterviewPrep entity = InterviewPrep.builder()
                                .user(user)
                                .application(application)
                                .questions(questionsJson)
                                .build();
                entity = interviewPrepRepository.save(entity);

                // Save to history
                try {
                        String title = "Interview Prep: " + request.jobTitle()
                                        + (request.company() != null ? " at " + request.company() : "");
                        historyService.createHistory(CreateHistoryRequest.builder()
                                        .type(HistoryType.INTERVIEW_PREP)
                                        .title(title)
                                        .content("Generated interview prep for " + request.jobTitle() + ". "
                                                        + rawList.size() + " questions.")
                                        .metadata(objectMapper.writeValueAsString(Map.of(
                                                        "jobTitle", request.jobTitle(),
                                                        "questionCount", rawList.size())))
                                        .build());
                } catch (Exception e) {
                        log.warn("Failed to save interview prep to history", e);
                }

                return InterviewPrepResponse.from(entity, objectMapper);
        }

        @Transactional(readOnly = true)
        public List<InterviewPrepResponse> listForCurrentUser() {
                User user = userService.getCurrentUser();
                return interviewPrepRepository
                                .findByUserIdOrderByCreatedAtDesc(user.getId())
                                .stream()
                                .map(e -> InterviewPrepResponse.from(e, objectMapper))
                                .toList();
        }

        @Transactional(readOnly = true)
        public InterviewPrepResponse getById(UUID id) {
                User user = userService.getCurrentUser();
                InterviewPrep entity = interviewPrepRepository.findById(id)
                                .filter(e -> e.getUser().getId().equals(user.getId()))
                                .orElseThrow(() -> new ResourceNotFoundException("InterviewPrep", id));
                return InterviewPrepResponse.from(entity, objectMapper);
        }
}
