package com.jobflow.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "openrouter", url = "${openrouter.base-url}")
public interface OpenRouterClient {

    @PostMapping("/chat/completions")
    OpenRouterResponse complete(
            @RequestHeader("Authorization") String authorization,
            @RequestHeader("HTTP-Referer") String referer,
            @RequestBody OpenRouterRequest request
    );
}
