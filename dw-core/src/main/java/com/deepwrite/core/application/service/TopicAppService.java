package com.deepwrite.core.application.service;

import com.deepwrite.api.model.dto.TopicDTO;
import com.deepwrite.api.model.request.GenerateTopicRequest;
import com.deepwrite.core.infrastructure.ai.GeminiClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class TopicAppService {

    private static final Logger log = LoggerFactory.getLogger(TopicAppService.class);

    private final GeminiClient geminiClient;
    private final ObjectMapper objectMapper;

    public TopicAppService(GeminiClient geminiClient, ObjectMapper objectMapper) {
        this.geminiClient = geminiClient;
        this.objectMapper = objectMapper;
    }

    private static final String TOPIC_GENERATION_PROMPT_TEMPLATE = 
            "You are a helpful academic advisor. Generate 3 research paper topics for a student with the following background:\n" +
            "- Major: %s\n" +
            "- Research Direction: %s\n" +
            "- Specific Interest: %s\n\n" +
            "Requirements:\n" +
            "1. Topics should be novel and feasible.\n" +
            "2. Provide a brief overview (2-3 sentences) for each topic.\n" +
            "3. Return the result STRICTLY as a JSON array of objects with keys: 'title', 'overview'.\n" +
            "4. Do not output any markdown formatting (like ```json), just the raw JSON string.\n" +
            "5. Language: Simplified Chinese.";

    public List<TopicDTO> generateTopics(GenerateTopicRequest request) {
        String prompt = String.format(TOPIC_GENERATION_PROMPT_TEMPLATE, 
                request.getMajor(), 
                request.getDirection(), 
                request.getResearchInterest() == null ? "None" : request.getResearchInterest());

        String responseContent = geminiClient.call(prompt);
        log.info("AI Response: {}", responseContent);

        try {
            // Clean up potential markdown code blocks
            String cleanJson = responseContent.replace("```json", "").replace("```", "").trim();
            return objectMapper.readValue(cleanJson, new TypeReference<List<TopicDTO>>() {});
        } catch (Exception e) {
            log.error("Failed to parse AI response", e);
            return Collections.emptyList();
        }
    }
}
