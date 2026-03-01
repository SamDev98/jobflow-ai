package com.jobflow.controller;

import com.jobflow.dto.response.HistoryResponse;
import com.jobflow.entity.enums.HistoryType;
import com.jobflow.service.HistoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HistoryController.class)
@SuppressWarnings("null")
class HistoryControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private HistoryService historyService;

  @Test
  @WithMockUser
  void getHistory_returns200() throws Exception {
    HistoryResponse response = HistoryResponse.builder()
        .id(UUID.randomUUID())
        .title("Action")
        .type(HistoryType.OTHER)
        .createdAt(Instant.now())
        .build();

    when(historyService.getAllHistory(isNull(), any()))
        .thenReturn(new PageImpl<>(List.of(response)));

    mockMvc.perform(get("/api/history"))
        .andExpect(status().isOk());
  }
}
