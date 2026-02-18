package com.jobflow.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class OpenRouterResponse {

    private List<Choice> choices;
    private Usage usage;

    @Getter
    @NoArgsConstructor
    public static class Choice {
        private Message message;
    }

    @Getter
    @NoArgsConstructor
    public static class Message {
        private String content;
    }

    @Getter
    @NoArgsConstructor
    public static class Usage {
        private int promptTokens;
        private int completionTokens;
        private int totalTokens;
    }

    public String getContent() {
        if (choices == null || choices.isEmpty()) return "";
        return choices.get(0).getMessage().getContent();
    }
}
