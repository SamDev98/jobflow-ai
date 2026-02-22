package com.jobflow.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobflow.dto.request.CreateHistoryRequest;
import com.jobflow.dto.response.OptimizedResumeResponse;
import com.jobflow.entity.JobApplication;
import com.jobflow.entity.Resume;
import com.jobflow.entity.User;
import com.jobflow.entity.enums.HistoryType;
import com.jobflow.exception.ResourceNotFoundException;
import com.jobflow.repository.JobApplicationRepository;
import com.jobflow.repository.ResumeRepository;
import com.jobflow.util.PromptTemplates;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ResumeOptimizerService {

        private final LLMOrchestrator llm;
        private final StorageService storageService;
        private final ResumeRepository resumeRepository;
        private final JobApplicationRepository applicationRepository;
        private final UserService userService;
        private final ObjectMapper objectMapper;
        private final HistoryService historyService;

        @Transactional
        public OptimizedResumeResponse optimize(MultipartFile resumeFile,
                        MultipartFile templateFile,
                        String jobDescription,
                        UUID applicationId) throws IOException {
                User user = userService.getCurrentUser();
                JobApplication application = applicationId != null
                                ? applicationRepository.findByIdAndUserIdAndDeletedAtIsNull(applicationId, user.getId())
                                                .orElseThrow(() -> new ResourceNotFoundException("Application",
                                                                applicationId))
                                : null;

                // 1. Extract text from .docx files
                String resumeText = extractDocxText(resumeFile);
                String templateText = templateFile != null ? extractDocxText(templateFile)
                                : "Standard professional tech resume style.";

                // 2. Build user context
                String userContext = buildUserContext(user);

                // 3. Generate optimizations (direct pass of JD text for context)
                String optimizePrompt = PromptTemplates.OPTIMIZE_RESUME.formatted(
                                templateText, resumeText, jobDescription, userContext);

                Map<String, Object> optimizations = objectMapper.convertValue(
                                llm.completeAsJson(optimizePrompt, user.getTier(), Map.class),
                                new TypeReference<Map<String, Object>>() {
                                });

                // 4. Upload original .docx to R2
                byte[] docxBytes = resumeFile.getBytes();
                String docxKey = "resumes/%s/%s.docx".formatted(user.getId(), UUID.randomUUID());
                storageService.upload(new ByteArrayInputStream(docxBytes), docxKey,
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                docxBytes.length);

                // PDF conversion placeholder
                String pdfKey = docxKey.replace(".docx", ".pdf");

                // 5. Calculate ATS Score (rough estimation based on simple word match for
                // keywords if available)
                List<String> keywordsApplied = asStringList(optimizations.get("skills_reordered"));
                int atsScore = calculateAtsScore(resumeText, keywordsApplied);

                // 6. Persist Resume entity
                Resume resume = Resume.builder()
                                .user(user)
                                .application(application)
                                .s3KeyDocx(docxKey)
                                .s3KeyPdf(pdfKey)
                                .atsScore(atsScore)
                                .changesMade(objectMapper.writeValueAsString(optimizations))
                                .build();
                resume = resumeRepository.save(resume);

                // 6.1 Save to history
                try {
                        String title = "Optimization: " + (application != null ? application.getCompany() : "Custom");
                        historyService.createHistory(CreateHistoryRequest.builder()
                                        .type(HistoryType.RESUME_OPTIMIZATION)
                                        .title(title)
                                        .content((String) optimizations.get("optimized_content"))
                                        .metadata(objectMapper.writeValueAsString(Map.of(
                                                        "jobDescription", jobDescription,
                                                        "atsScore", atsScore)))
                                        .build());
                } catch (Exception e) {
                        log.warn("Failed to save history for resume optimization", e);
                }

                // 7. Build response
                String optimizedContent = (String) optimizations.get("optimized_content");
                List<String> skillsReordered = asStringList(optimizations.get("skills_reordered"));
                List<Map<String, Object>> changes = asMapList(optimizations.get("experience_changes"));

                List<OptimizedResumeResponse.ExperienceChange> experienceChanges = changes.stream()
                                .map(c -> new OptimizedResumeResponse.ExperienceChange(
                                                (String) c.get("role"),
                                                (String) c.get("original_bullet"),
                                                (String) c.get("optimized_bullet"),
                                                asStringList(c.get("keywords_added"))))
                                .toList();

                return new OptimizedResumeResponse(
                                resume.getId(),
                                storageService.getPresignedUrl(docxKey, 1),
                                storageService.getPresignedUrl(pdfKey, 1),
                                resume.getAtsScore(),
                                optimizedContent,
                                skillsReordered,
                                experienceChanges);
        }

        private String extractDocxText(MultipartFile file) throws IOException {
                try (XWPFDocument doc = new XWPFDocument(file.getInputStream());
                                XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
                        return extractor.getText();
                }
        }

        private int calculateAtsScore(String resumeText, List<String> keywords) {
                if (keywords.isEmpty())
                        return 0;
                String lowerResume = resumeText.toLowerCase();
                long matches = keywords.stream()
                                .filter(kw -> lowerResume.contains(kw.toLowerCase()))
                                .count();
                return (int) Math.round((double) matches / keywords.size() * 100);
        }

        private String buildUserContext(User user) {
                var profile = user.getProfile();
                if (profile == null)
                        return "Candidate profile not configured.";
                return """
                                candidate:
                                  years_experience: %d
                                  tech_stack: %s
                                  location: %s
                                  work_mode: %s
                                """.formatted(
                                profile.getYearsExperience(),
                                profile.getTechStack() != null ? String.join(", ", profile.getTechStack()) : "N/A",
                                profile.getLocation(),
                                profile.getWorkMode());
        }

        private List<String> asStringList(Object value) {
                if (value == null) {
                        return List.of();
                }

                return objectMapper.convertValue(value, new TypeReference<List<String>>() {
                });
        }

        private List<Map<String, Object>> asMapList(Object value) {
                if (value == null) {
                        return List.of();
                }

                return objectMapper.convertValue(value, new TypeReference<List<Map<String, Object>>>() {
                });
        }
}
