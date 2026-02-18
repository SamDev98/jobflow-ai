package com.jobflow.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobflow.client.OpenRouterClient;
import com.jobflow.client.OpenRouterRequest;
import com.jobflow.entity.enums.Tier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class LLMOrchestrator {

    private final OpenRouterClient openRouterClient;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Value("${openrouter.api-key}")
    private String apiKey;

    @Value("${openrouter.default-model}")
    private String defaultModel;

    @Value("${openrouter.pro-model}")
    private String proModel;

    private static final Duration CACHE_TTL = Duration.ofDays(7);

    public String complete(String prompt, Tier tier) {
        return complete(prompt, tier, false);
    }

    public String complete(String prompt, Tier tier, boolean jsonOutput) {
        String model = tier == Tier.PRO ? proModel : defaultModel;
        String cacheKey = "llm:" + model + ":" + prompt.hashCode();

        Object cached = redisTemplate.opsForValue().get(cacheKey);
        if (cached != null) {
            log.debug("LLM cache hit for key: {}", cacheKey);
            return cached.toString();
        }

        OpenRouterRequest.ResponseFormat responseFormat = jsonOutput ? OpenRouterRequest.ResponseFormat.json() : null;

        OpenRouterRequest request = OpenRouterRequest.builder()
                .model(model)
                .messages(List.of(OpenRouterRequest.Message.user(prompt)))
                .responseFormat(responseFormat)
                .temperature(jsonOutput ? 0.3 : 0.7)
                .build();

        String response = openRouterClient
                .complete("Bearer " + apiKey, "https://jobflow.ai", request)
                .getContent();

        redisTemplate.opsForValue().set(cacheKey, response, CACHE_TTL);
        return response;
    }

    public <T> T completeAsJson(String prompt, Tier tier, Class<T> type) {
        String response = complete(prompt, tier, true);
        try {
            return objectMapper.readValue(response, type);
        } catch (Exception e) {
            log.error("Failed to parse LLM JSON response: {}", response, e);
            throw new IllegalStateException("LLM returned invalid JSON", e);
        }
    }
}
