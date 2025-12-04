package com.deepwrite.core.infrastructure.ai;

public class TopicAnalysisPrompt {

    public static String build(String topic, String fileContent) {
        return String.format("""
                You are an expert academic research consultant. A student has proposed a research topic and provided some background material.
                
                **Proposed Topic:**
                %s
                
                **Background Material Content:**
                %s
                
                **Your Task:**
                Analyze the feasibility and innovation of this topic based on the provided material.
                
                **Output Format:**
                Return a JSON object with the following fields:
                - "feasibility": (String) High/Medium/Low, with a brief explanation in Chinese.
                - "innovation": (String) High/Medium/Low, with a brief explanation in Chinese.
                - "suggestions": (Array of Strings) 3-5 specific suggestions to improve the topic in Chinese.
                - "refined_topic": (String) A suggested refined version of the topic (optional) in Chinese.
                
                **Important:** 
                - The response MUST be in Chinese, except for specific academic terms that are better expressed in English.
                - Ensure the JSON is valid and strictly follows this format.
                """, topic, fileContent);
    }
}
