package com.deepwrite.core.interfaces.web;

import com.deepwrite.api.model.dto.TopicDTO;
import com.deepwrite.api.model.request.GenerateTopicRequest;
import com.deepwrite.core.application.service.TopicAppService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/topic")
public class TopicController {

    private final TopicAppService topicAppService;

    public TopicController(TopicAppService topicAppService) {
        this.topicAppService = topicAppService;
    }

    @PostMapping("/generate")
    public List<TopicDTO> generateTopics(@RequestBody GenerateTopicRequest request) {
        return topicAppService.generateTopics(request);
    }
}