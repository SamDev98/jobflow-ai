package com.jobflow.util;

public final class PromptTemplates {

    private PromptTemplates() {}

    public static final String EXTRACT_KEYWORDS = """
            Extract 15-20 ATS keywords from this job description.
            Focus on: technologies, frameworks, methodologies, hard skills.

            Job Description:
            %s

            Output a JSON array of strings only. Example: ["Java", "Spring Boot", "Kafka"]
            """;

    public static final String OPTIMIZE_RESUME = """
            You are a career assistant for a software engineer.

            Candidate Context:
            %s

            Current Resume:
            %s

            Target Job Keywords: %s

            Tasks:
            1. Reorder "Technical Skills" to prioritize job keywords
            2. Rewrite 3-5 experience bullets to include keywords naturally
            3. Do NOT fabricate experience — only rephrase existing content
            4. Preserve original meaning

            Output JSON only:
            {
              "skills_reordered": ["Java", "Spring Boot", ...],
              "experience_changes": [
                {
                  "role": "Senior Engineer at Company X",
                  "original_bullet": "...",
                  "optimized_bullet": "...",
                  "keywords_added": ["Kafka", "microservices"]
                }
              ]
            }
            """;

    public static final String SALARY_RESEARCH = """
            You are a salary advisor for remote tech roles.

            Data collected from job boards and salary sites:
            %s

            Candidate: %d years of experience, working remotely from %s, applying to a US-based company.

            Synthesize a USD salary range considering the typical remote discount (20-30%%).

            Output JSON only:
            {
              "range_usd": {"low": 0, "mid": 0, "high": 0},
              "reasoning": "...",
              "confidence": 7
            }
            """;

    public static final String INTERVIEW_PREP = """
            Generate interview questions for a %s position at %s.

            Job Description:
            %s

            Candidate Tech Stack: %s

            Generate 10 questions with varying difficulty (easy/medium/hard).

            Output JSON only:
            [
              {
                "question": "...",
                "answer_outline": "...",
                "difficulty": "medium"
              }
            ]
            """;
}
