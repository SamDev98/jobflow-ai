package com.jobflow.client;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class OpenRouterRequest {

    private String model;
    private List<Message> messages;

    @JsonProperty("response_format")
    private ResponseFormat responseFormat;

    @Builder.Default
    private double temperature = 0.7;

    @Getter
    @Builder
    public static class Message {
        private String role;
        private String content;

        public static Message user(String content) {
            return Message.builder().role("user").content(content).build();
        }

        public static Message system(String content) {
            return Message.builder().role("system").content(content).build();
        }
    }

    @Getter
    @Builder
    public static class ResponseFormat {
        private String type;

        public static ResponseFormat json() {
            return ResponseFormat.builder().type("json_object").build();
        }
    }
}
