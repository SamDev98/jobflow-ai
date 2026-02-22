package com.jobflow.util;

public final class PromptTemplates {

  private PromptTemplates() {
  }

  public static final String EXTRACT_KEYWORDS = """
      Extract 15-20 ATS keywords from this job description.
      Focus on: technologies, frameworks, methodologies, hard skills.

      Job Description:
      %s

      Output a JSON array of strings only. Example: ["Java", "Spring Boot", "Kafka"]
      """;

  public static final String OPTIMIZE_RESUME = """
      You are an expert career consultant specializing in ATS-optimized resumes for software engineers.

      Resume Template (Style Reference):
      %s

      Candidate's Base Resume:
      %s

      Target Job Description (JD):
      %s

      Candidate Context:
      %s

      IMPORTANT RULES:
      1. STRUCTURE: Reorganize the Base Resume following the exact structure and style of the template provided.
      2. CONTENT: Use ONLY the information (experience, education, certifications) present in the Base Resume.
      3. FORBIDDEN: Do NOT invent or fabricate any skills, roles, or achievements not present in the Base Resume.
      4. LENGTH: Ensure the final content is concise and would fit in under 2 pages (approx. 800-1000 words max).
      5. ATS FOCUS: Naturally weave in keywords from the JD into the experience bullets and skills section.
      6. LANGUAGE: Maintain the language of the original resume (PT-BR).

      Output a valid JSON object with the following structure:
      {
        "optimized_content": "Full Markdown/String representation of the new resume",
        "skills_reordered": ["Skill A", "Skill B", ...],
        "experience_changes": [
          {
            "role": "Role Name",
            "original_bullet": "Original text",
            "optimized_bullet": "ATS-optimized text",
            "keywords_added": ["Keyword X", "Keyword Y"]
          }
        ],
        "formatting_notes": "Brief explanation of how it fits the template"
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
