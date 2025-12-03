package com.deepwrite.core.controller;

import com.deepwrite.api.service.TopicAppService;
import com.deepwrite.common.model.Response;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/projects/{projectId}/topics")
public class TopicController {

    @Autowired
    private TopicAppService topicAppService;

    @PostMapping("/generate")
    public Response<List<Object>> generateTopics(@PathVariable("projectId") Long projectId, @RequestBody GenerateRequest request) {
        return topicAppService.generateTopics(projectId, request.getInitialIdea());
    }

    @PostMapping("/confirm")
    public Response<Boolean> confirmTopic(@PathVariable("projectId") Long projectId, @RequestBody com.deepwrite.api.dto.ConfirmTopicRequest request) {
        return topicAppService.confirmTopic(projectId, request);
    }

    @Data
    public static class GenerateRequest {
        private String initialIdea;

        public String getInitialIdea() {
            return initialIdea;
        }

        public void setInitialIdea(String initialIdea) {
            this.initialIdea = initialIdea;
        }
    }
}
