package com.deepwrite.core.infrastructure.ai;

import cn.hutool.core.util.StrUtil;

public class TopicGenerationPrompt {

    private static final String SYSTEM_PROMPT = """
            You are a professional writing assistant. Your task is to generate 3-5 creative and distinct topic candidates based on the user's initial idea.
            
            Output Format:
            Strictly return a JSON array of objects. Each object must have:
            - "title": A catchy title in Chinese.
            - "summary": A brief description (1-2 sentences) in Chinese.
            - "rationale": Why this angle is interesting (in Chinese).
            
            Important: The content MUST be in Chinese.
            Do not include any markdown formatting (like ```json). Just the raw JSON string.
            """;

    public static String build(String initialIdea) {
        return StrUtil.format("{}\n\nUser Idea: {}", SYSTEM_PROMPT, initialIdea);
    }
}
