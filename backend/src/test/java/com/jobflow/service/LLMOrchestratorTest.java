package com.jobflow.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobflow.client.OpenRouterRequest;
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
import org.springframework.web.client.RestClient;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("null")
class LLMOrchestratorTest {

    @Mock
    private RestClient restClient;

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
        ReflectionTestUtils.setField(llmOrchestrator, "baseUrl", "https://openrouter.ai/api/v1");
        ReflectionTestUtils.setField(llmOrchestrator, "defaultModel", "free-model");
        ReflectionTestUtils.setField(llmOrchestrator, "proModel", "pro-model");
        ReflectionTestUtils.setField(llmOrchestrator, "restClient", restClient);
    }

    @Test
    void complete_withCacheHit_doesNotCallOpenRouter() {
        String prompt = "Hello";
        String cachedResponse = "Cached hi";
        when(valueOperations.get(anyString())).thenReturn(cachedResponse);

        String result = llmOrchestrator.complete(prompt, Tier.FREE);

        assertThat(result).isEqualTo(cachedResponse);
        verify(restClient, never()).post();
    }

    @Test
    void complete_withCacheMiss_callsOpenRouterAndCaches() {
        String prompt = "Hello";
        String apiResponse = "API hi";

        when(valueOperations.get(anyString())).thenReturn(null);

        var uriSpec = mock(RestClient.RequestBodyUriSpec.class);
        var bodySpec = mock(RestClient.RequestBodySpec.class);
        var responseSpec = mock(RestClient.ResponseSpec.class);
        var openRouterResponse = mock(OpenRouterResponse.class);

        when(restClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(bodySpec);
        when(bodySpec.header(anyString(), anyString())).thenReturn(bodySpec);
        when(bodySpec.body(any(OpenRouterRequest.class))).thenAnswer(i -> bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(OpenRouterResponse.class)).thenReturn(openRouterResponse);
        when(openRouterResponse.getContent()).thenReturn(apiResponse);

        String result = llmOrchestrator.complete(prompt, Tier.FREE);

        assertThat(result).isEqualTo(apiResponse);
        verify(restClient).post();
        verify(valueOperations).set(anyString(), eq(apiResponse), eq(Duration.ofDays(7)));
    }
}
