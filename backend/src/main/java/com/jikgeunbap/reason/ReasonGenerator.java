package com.jikgeunbap.reason;

/**
 * 추천 이유(자연어 한두 문장) 생성기 추상화.
 * 구현체: RuleBasedReasonGenerator (템플릿), ClaudeReasonGenerator (LLM + fallback)
 */
public interface ReasonGenerator {
    String generate(ReasonContext context);
}
