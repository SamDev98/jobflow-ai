package com.jobflow.controller;

import com.jobflow.entity.User;
import com.jobflow.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@SuppressWarnings("null")
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private UserService userService;

  @Test
  @WithMockUser
  void sync_returns200_andCallsService() throws Exception {
    when(userService.getOrCreateCurrentUser("user@example.com"))
        .thenReturn(User.builder().email("user@example.com").build());

    mockMvc.perform(post("/users/sync")
        .with(csrf())
        .param("email", "user@example.com"))
        .andExpect(status().isOk());

    verify(userService).getOrCreateCurrentUser("user@example.com");
  }
}
