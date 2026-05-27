package com.jikgeunbap.reason;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * Anthropic Messages API HTTP 클라이언트.
 * SDK 의존성 없이 RestClient만 사용.
 *
 * Endpoint: POST https://api.anthropic.com/v1/messages
 * Docs: https://docs.anthropic.com/en/api/messages
 */
@Component
public class ClaudeReasonClient {

    private static final String API_URL     = "https://api.anthropic.com/v1/messages";
    private static final String API_VERSION = "2023-06-01";
    private static final String MODEL       = "claude-haiku-4-5";
    private static final int    MAX_TOKENS  = 200;

    private final RestClient restClient;
    private final String     apiKey;

    public ClaudeReasonClient(@Value("${anthropic.api-key:}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder()
                .requestFactory(makeFactory())
                .build();
    }

    /** API 키가 설정돼 있으면 LLM 호출 가능. */
    public boolean isEnabled() {
        return apiKey != null && !apiKey.isBlank();
    }

    /**
     * 시스템 프롬프트 + 유저 메시지 → Claude 응답 텍스트.
     * 호출 측에서 try-catch로 fallback 처리 필요.
     */
    public String generate(String systemPrompt, String userMessage) {
        Map<String, Object> body = Map.of(
                "model",      MODEL,
                "max_tokens", MAX_TOKENS,
                "system",     systemPrompt,
                "messages",   List.of(Map.of("role", "user", "content", userMessage))
        );

        AnthropicResponse response = restClient.post()
                .uri(API_URL)
                .header("x-api-key", apiKey)
                .header("anthropic-version", API_VERSION)
                .header("content-type", "application/json")
                .body(body)
                .retrieve()
                .body(AnthropicResponse.class);

        if (response == null || response.content() == null || response.content().isEmpty()) {
            throw new IllegalStateException("Empty response from Anthropic API");
        }
        return response.content().get(0).text();
    }

    /** 짧은 응답 위주이므로 짧은 타임아웃으로 빠른 fallback 유도. */
    private static org.springframework.http.client.SimpleClientHttpRequestFactory makeFactory() {
        var factory = new org.springframework.http.client.SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(3).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(8).toMillis());
        return factory;
    }

    // ── 응답 매핑 (필요한 필드만) ──────────────────────────────────────────

    @JsonIgnoreProperties(ignoreUnknown = true)
    record AnthropicResponse(List<ContentBlock> content) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    record ContentBlock(String type, String text) {}
}
