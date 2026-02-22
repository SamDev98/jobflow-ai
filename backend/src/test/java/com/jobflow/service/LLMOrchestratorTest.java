package com.jobflow.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobflow.client.OpenRouterClient;
import com.jobflow.client.OpenRouterResponse;
import com.jobflow.entity.enums.Tier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class LLMOrchestratorTest {

  @Mock
  private OpenRouterClient openRouterClient;

  @Mock
  private RedisTemplate<String, Object> redisTemplate;

  @Mock
  private ValueOperations<String, Object> valueOperations;

  @Mock
  private ObjectMapper objectMapper;

  @InjectMocks
  private LLMOrchestrator llmOrchestrator;

  @BeforeEach
  void setUp() {
    lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    ReflectionTestUtils.setField(llmOrchestrator, "apiKey", "test-key");
    ReflectionTestUtils.setField(llmOrchestrator, "defaultModel", "free-model");
    ReflectionTestUtils.setField(llmOrchestrator, "proModel", "pro-model");
  }

  @Test
  void complete_withCacheHit_doesNotCallOpenRouter() {
    String prompt = "Hello";
    String cachedResponse = "Cached hi";
    when(valueOperations.get(anyString())).thenReturn(cachedResponse);

    String result = llmOrchestrator.complete(prompt, Tier.FREE);

    assertThat(result).isEqualTo(cachedResponse);
    verify(openRouterClient, never()).complete(any(), any(), any());
  }

  @Test
  void complete_withCacheMiss_callsOpenRouterAndCaches() {
    String prompt = "Hello";
    String apiResponse = "API hi";

    when(valueOperations.get(anyString())).thenReturn(null);

    OpenRouterResponse response = mock(OpenRouterResponse.class);
    when(response.getContent()).thenReturn(apiResponse);
    when(openRouterClient.complete(anyString(), anyString(), any())).thenReturn(response);

    String result = llmOrchestrator.complete(prompt, Tier.FREE);

    assertThat(result).isEqualTo(apiResponse);
    verify(openRouterClient).complete(eq("Bearer test-key"), anyString(), any());
    verify(valueOperations).set(anyString(), eq(apiResponse), eq(Duration.ofDays(7)));
  }
}
