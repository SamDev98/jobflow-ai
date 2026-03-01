package com.jobflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobflow.dto.request.UpdateProfileRequest;
import com.jobflow.dto.response.ProfileResponse;
import com.jobflow.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfileController.class)
@SuppressWarnings("null")
class ProfileControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private UserService userService;

  @Test
  @WithMockUser
  void get_returns200() throws Exception {
    when(userService.getProfile())
        .thenReturn(new ProfileResponse(5, List.of("Java"), "Remote", "Remote", 120000));

    mockMvc.perform(get("/profile"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.yearsExperience").value(5))
        .andExpect(jsonPath("$.location").value("Remote"));
  }

  @Test
  @WithMockUser
  void update_returns200() throws Exception {
    UpdateProfileRequest request = new UpdateProfileRequest(6, List.of("Java", "Spring"), "Remote", "Hybrid", 130000);

    when(userService.updateProfile(any(UpdateProfileRequest.class)))
        .thenReturn(new ProfileResponse(6, List.of("Java", "Spring"), "Remote", "Hybrid", 130000));

    mockMvc.perform(put("/profile")
        .with(csrf())
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.yearsExperience").value(6))
        .andExpect(jsonPath("$.workMode").value("Hybrid"));
  }
}
