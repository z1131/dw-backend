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

@Component
public class DeepSeekClient {

    private static final Logger log = LoggerFactory.getLogger(DeepSeekClient.class);

    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${deepseek.base-url:https://api.deepseek.com}")
    private String baseUrl;

    public String generateTopics(String initialIdea) {
        String prompt = TopicGenerationPrompt.build(initialIdea);
        String url = StrUtil.format("{}/chat/completions", baseUrl);

        // Construct DeepSeek (OpenAI-compatible) Request Body
        JSONObject message = new JSONObject()
                .set("role", "user")
                .set("content", prompt);
        
        JSONObject requestBody = new JSONObject()
                .set("model", "deepseek-chat")
                .set("messages", new JSONArray().put(message))
                .set("stream", false)
                .set("temperature", 0.7);

        log.info("Calling DeepSeek API for topic generation...");
        try (HttpResponse response = HttpRequest.post(url)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .timeout(60000) // 60s timeout
                .execute()) {

            if (!response.isOk()) {
                log.error("DeepSeek API failed: {} - {}", response.getStatus(), response.body());
                throw new RuntimeException("AI Service Unavailable: " + response.getStatus());
            }

            String responseBody = response.body();
            return parseDeepSeekResponse(responseBody);

        } catch (Exception e) {
            log.error("Error calling DeepSeek API", e);
            throw new RuntimeException("Failed to generate topics", e);
        }
    }

    public String analyzeTopic(String topic, String fileContent) {
        String prompt = TopicAnalysisPrompt.build(topic, fileContent);
        String url = StrUtil.format("{}/chat/completions", baseUrl);

        JSONObject message = new JSONObject();
        message.set("role", "user");
        message.set("content", prompt);

        JSONArray messages = new JSONArray();
        messages.add(message);

        JSONObject requestBody = new JSONObject();
        requestBody.set("model", "deepseek-chat");
        requestBody.set("messages", messages);
        requestBody.set("temperature", 0.7);

        try (HttpResponse response = HttpRequest.post(url)
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .body(requestBody.toString())
                .timeout(60000)
                .execute()) {

            if (!response.isOk()) {
                log.error("DeepSeek API failed: {} - {}", response.getStatus(), response.body());
                throw new RuntimeException("DeepSeek API returned " + response.getStatus());
            }

            String responseBody = response.body();
            return parseDeepSeekResponse(responseBody);
        } catch (Exception e) {
            log.error("DeepSeek API error", e);
            throw new RuntimeException("DeepSeek API call failed", e);
        }
    }

    private String parseDeepSeekResponse(String responseBody) {
        try {
            JSONObject json = JSONUtil.parseObj(responseBody);
            // Navigate: choices[0].message.content
            String text = json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getStr("content");
            
            // Clean up potential markdown code blocks
            return StrUtil.cleanBlank(text.replace("```json", "").replace("```", ""));
        } catch (Exception e) {
            log.error("Failed to parse DeepSeek response: {}", responseBody, e);
            throw new RuntimeException("Invalid AI Response Format");
        }
    }
}
