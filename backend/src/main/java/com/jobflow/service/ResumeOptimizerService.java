package com.jobflow.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobflow.dto.response.OptimizedResumeResponse;
import com.jobflow.entity.JobApplication;
import com.jobflow.entity.Resume;
import com.jobflow.entity.User;
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

        @Transactional
        public OptimizedResumeResponse optimize(MultipartFile resumeFile,
                        String jobDescription,
                        UUID applicationId) throws IOException {
                User user = userService.getCurrentUser();
                JobApplication application = applicationId != null
                                ? applicationRepository.findByIdAndUserIdAndDeletedAtIsNull(applicationId, user.getId())
                                                .orElseThrow(() -> new ResourceNotFoundException("Application",
                                                                applicationId))
                                : null;

                // 1. Extract text from .docx
                String resumeText = extractDocxText(resumeFile);

                // 2. Extract ATS keywords from JD
                String keywordsPrompt = PromptTemplates.EXTRACT_KEYWORDS.formatted(jobDescription);
                List<?> rawKeywords = llm.completeAsJson(keywordsPrompt, user.getTier(), List.class);
                List<String> keywords = rawKeywords.stream()
                                .map(String::valueOf)
                                .toList();

                // 3. Build user context
                String userContext = buildUserContext(user);

                // 4. Generate optimizations
                String optimizePrompt = PromptTemplates.OPTIMIZE_RESUME.formatted(
                                userContext, resumeText, String.join(", ", keywords));
                Map<String, Object> optimizations = objectMapper.convertValue(
                                llm.completeAsJson(optimizePrompt, user.getTier(), Map.class),
                                new TypeReference<Map<String, Object>>() {
                                });

                // 5. Upload original .docx to R2
                byte[] docxBytes = resumeFile.getBytes();
                String docxKey = "resumes/%s/%s.docx".formatted(user.getId(), UUID.randomUUID());
                storageService.upload(new ByteArrayInputStream(docxBytes), docxKey,
                                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                                docxBytes.length);

                // PDF conversion placeholder — integrate LibreOffice or a PDF lib here
                String pdfKey = docxKey.replace(".docx", ".pdf");

                // 6. Persist Resume entity
                Resume resume = Resume.builder()
                                .user(user)
                                .application(application)
                                .s3KeyDocx(docxKey)
                                .s3KeyPdf(pdfKey)
                                .atsScore(calculateAtsScore(resumeText, keywords))
                                .changesMade(objectMapper.writeValueAsString(optimizations))
                                .build();
                resume = resumeRepository.save(resume);

                // 7. Build response
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
