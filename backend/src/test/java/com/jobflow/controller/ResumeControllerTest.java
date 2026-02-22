package com.jobflow.controller;

import com.jobflow.dto.response.OptimizedResumeResponse;
import com.jobflow.service.ResumeOptimizerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResumeController.class)
@SuppressWarnings("null")
class ResumeControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private ResumeOptimizerService resumeOptimizerService;

  @Test
  @WithMockUser
  void optimize_returns200_withMultipart() throws Exception {
    MockMultipartFile resume = new MockMultipartFile(
        "resume",
        "resume.docx",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "fake-docx-content".getBytes());
    MockMultipartFile template = new MockMultipartFile(
        "template",
        "template.docx",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "fake-template-content".getBytes());
    MockMultipartFile jd = new MockMultipartFile(
        "jd",
        "",
        "text/plain",
        "Backend Engineer JD".getBytes());

    OptimizedResumeResponse response = new OptimizedResumeResponse(
        UUID.randomUUID(),
        "https://example.com/resume.docx",
        "https://example.com/resume.pdf",
        88,
        "Optimized content",
        List.of("Java", "Spring"),
        List.of());

    when(resumeOptimizerService.optimize(any(), any(), eq("Backend Engineer JD"), isNull()))
        .thenReturn(response);

    mockMvc.perform(multipart("/resumes/optimize")
        .file(resume)
        .file(template)
        .file(jd)
        .with(csrf()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.atsScore").value(88))
        .andExpect(jsonPath("$.skillsReordered[0]").value("Java"));
  }
}
