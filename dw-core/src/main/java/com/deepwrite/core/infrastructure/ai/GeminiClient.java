package com.deepwrite.core.infrastructure.ai;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class GeminiClient {

    private static final Logger log = LoggerFactory.getLogger(GeminiClient.class);

    @Value("${spring.ai.google.gemini.api-key}")
    private String apiKey;

    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key={}";

    public String generateTopics(String initialIdea) {
        String prompt = TopicGenerationPrompt.build(initialIdea);
        String url = StrUtil.format(API_URL, apiKey);

        // Construct Gemini Request Body
        JSONObject contentPart = new JSONObject().set("text", prompt);
        JSONObject parts = new JSONObject().set("parts", new JSONArray().put(contentPart));
        JSONObject requestBody = new JSONObject().set("contents", new JSONArray().put(parts));

        log.info("Calling Gemini API for topic generation...");
        try (HttpResponse response = HttpRequest.post(url)
                .body(requestBody.toString())
                .timeout(30000) // 30s timeout
                .execute()) {

            if (!response.isOk()) {
                log.error("Gemini API failed: {} - {}", response.getStatus(), response.body());
                throw new RuntimeException("AI Service Unavailable");
            }

            String responseBody = response.body();
            return parseGeminiResponse(responseBody);

        } catch (Exception e) {
            log.error("Error calling Gemini API", e);
            throw new RuntimeException("Failed to generate topics", e);
        }
    }

    private String parseGeminiResponse(String responseBody) {
        try {
            JSONObject json = JSONUtil.parseObj(responseBody);
            // Navigate: candidates[0].content.parts[0].text
            String text = json.getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getStr("text");
            
            // Clean up potential markdown code blocks if the prompt instruction failed
            return StrUtil.cleanBlank(text.replace("```json", "").replace("```", ""));
        } catch (Exception e) {
            log.error("Failed to parse Gemini response: {}", responseBody, e);
            throw new RuntimeException("Invalid AI Response Format");
        }
    }
}
