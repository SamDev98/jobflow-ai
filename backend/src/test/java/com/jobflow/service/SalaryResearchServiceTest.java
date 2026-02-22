package com.jobflow.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobflow.dto.response.SalaryRangeResponse;
import com.jobflow.entity.SalaryResearch;
import com.jobflow.entity.User;
import com.jobflow.entity.UserProfile;
import com.jobflow.entity.enums.Tier;
import com.jobflow.repository.SalaryResearchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class SalaryResearchServiceTest {

  @Mock
  private LLMOrchestrator llm;
  @Mock
  private SalaryResearchRepository salaryResearchRepository;
  @Mock
  private UserService userService;
  @Mock
  private HistoryService historyService;

  private SalaryResearchService salaryResearchService;

  @BeforeEach
  void setUp() {
    salaryResearchService = new SalaryResearchService(
        llm,
        salaryResearchRepository,
        userService,
        new ObjectMapper(),
        historyService);
  }

  @Test
  void research_returnsParsedRangeAndDefaults() throws Exception {
    UserProfile profile = UserProfile.builder()
        .yearsExperience(5)
        .location("Remote")
        .build();
    User user = User.builder()
        .id(UUID.randomUUID())
        .tier(Tier.FREE)
        .clerkId("clerk_1")
        .email("user@example.com")
        .profile(profile)
        .build();
    profile.setUser(user);

    when(userService.getCurrentUser()).thenReturn(user);
    when(llm.completeAsJson(anyString(), eq(Tier.FREE), eq(Map.class)))
        .thenReturn(Map.of(
            "range_usd", Map.of("low", 120000, "mid", 150000, "high", 180000),
            "reasoning", "Based on market data"));
    when(salaryResearchRepository.save(any(SalaryResearch.class)))
        .thenAnswer(invocation -> {
          SalaryResearch entity = invocation.getArgument(0);
          entity.setId(UUID.randomUUID());
          return entity;
        });

    SalaryRangeResponse response = salaryResearchService.research(
        "Backend Engineer",
        "Acme",
        null,
        null);

    assertEquals(120000, response.rangeLowUsd());
    assertEquals(150000, response.rangeMidUsd());
    assertEquals(180000, response.rangeHighUsd());
    assertEquals(5, response.confidenceScore());
  }
}
