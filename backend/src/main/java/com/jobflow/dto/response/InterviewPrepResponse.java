package com.jobflow.dto.response;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobflow.entity.InterviewPrep;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record InterviewPrepResponse(
        UUID id,
        UUID applicationId,
        List<Question> questions,
        Instant createdAt
) {

    public record Question(
            String question,
            String answerOutline,
            String difficulty
    ) {}

    public static InterviewPrepResponse from(InterviewPrep entity, ObjectMapper mapper) {
        List<Question> questions;
        try {
            List<RawQuestion> raw = mapper.readValue(
                    entity.getQuestions(),
                    new TypeReference<List<RawQuestion>>() {}
            );
            questions = raw.stream()
                    .map(q -> new Question(q.question(), q.answer_outline(), q.difficulty()))
                    .toList();
        } catch (Exception e) {
            questions = List.of();
        }
        return new InterviewPrepResponse(
                entity.getId(),
                entity.getApplication() != null ? entity.getApplication().getId() : null,
                questions,
                entity.getCreatedAt()
        );
    }

    // Internal record matching LLM JSON field names
    private record RawQuestion(String question, String answer_outline, String difficulty) {}
}
