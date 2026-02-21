package com.jobflow.controller;

import com.jobflow.dto.response.SalaryRangeResponse;
import com.jobflow.service.SalaryResearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SalaryController.class)
@SuppressWarnings("null")
class SalaryControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private SalaryResearchService salaryResearchService;

  @Test
  @WithMockUser
  void research_returns200() throws Exception {
    SalaryRangeResponse response = new SalaryRangeResponse(
        UUID.randomUUID(),
        120000,
        150000,
        180000,
        "Based on market data",
        7);

    when(salaryResearchService.research(
        eq("Backend Engineer"),
        eq("Acme"),
        eq("Remote"),
        isNull())).thenReturn(response);

    mockMvc.perform(post("/salary/research")
        .with(csrf())
        .param("jobTitle", "Backend Engineer")
        .param("company", "Acme")
        .param("location", "Remote"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.rangeLowUsd").value(120000))
        .andExpect(jsonPath("$.rangeMidUsd").value(150000))
        .andExpect(jsonPath("$.rangeHighUsd").value(180000));
  }
}
