package com.jobflow.dto.response;

import java.util.List;
import java.util.UUID;

public record OptimizedResumeResponse(
                UUID resumeId,
                String docxUrl,
                String pdfUrl,
                Integer atsScore,
                String optimizedContent,
                List<String> skillsReordered,
                List<ExperienceChange> experienceChanges) {
        public record ExperienceChange(
                        String role,
                        String originalBullet,
                        String optimizedBullet,
                        List<String> keywordsAdded) {
        }
}
