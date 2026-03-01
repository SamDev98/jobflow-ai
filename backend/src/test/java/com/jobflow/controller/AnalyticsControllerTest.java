package com.jobflow.controller;

import com.jobflow.entity.User;
import com.jobflow.entity.enums.Stage;
import com.jobflow.repository.JobApplicationRepository;
import com.jobflow.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnalyticsController.class)
class AnalyticsControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private JobApplicationRepository applicationRepository;

  @MockitoBean
  private UserService userService;

  @Test
  @WithMockUser
  void byStage_returnsAllStagesWithCounts() throws Exception {
    UUID userId = UUID.randomUUID();
    User user = User.builder().id(userId).build();

    when(userService.getCurrentUser()).thenReturn(user);
    when(applicationRepository.countByStageForUser(userId))
        .thenReturn(List.of(
            new Object[] { Stage.APPLIED, 2L },
            new Object[] { Stage.REJECTED, 1L }));

    mockMvc.perform(get("/analytics/by-stage"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.APPLIED").value(2))
        .andExpect(jsonPath("$.REJECTED").value(1))
        .andExpect(jsonPath("$.SCREENING").value(0));
  }
}
