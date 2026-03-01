package com.jobflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobflow.dto.response.InterviewPrepResponse;
import com.jobflow.service.InterviewPrepService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InterviewPrepController.class)
@SuppressWarnings("null")
class InterviewPrepControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private InterviewPrepService interviewPrepService;

  @Test
  @WithMockUser
  void generate_returns201() throws Exception {
    when(interviewPrepService.generate(any())).thenReturn(buildResponse());

    mockMvc.perform(post("/interview-prep")
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(Map.of("jobTitle", "Backend Engineer"))))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.questions[0].question").value("Q1"));
  }

  @Test
  @WithMockUser
  void list_returns200() throws Exception {
    when(interviewPrepService.listForCurrentUser()).thenReturn(List.of(buildResponse()));

    mockMvc.perform(get("/interview-prep"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].questions[0].question").value("Q1"));
  }

  @Test
  @WithMockUser
  void getById_returns200() throws Exception {
    UUID id = UUID.randomUUID();
    when(interviewPrepService.getById(id)).thenReturn(buildResponse());

    mockMvc.perform(get("/interview-prep/{id}", id))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.questions[0].question").value("Q1"));
  }

  private InterviewPrepResponse buildResponse() {
    return new InterviewPrepResponse(
        UUID.randomUUID(),
        null,
        List.of(new InterviewPrepResponse.Question("Q1", "A1", "easy")),
        Instant.now());
  }
}
