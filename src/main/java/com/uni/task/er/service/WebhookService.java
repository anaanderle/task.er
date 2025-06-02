package com.uni.task.er.service;

import com.uni.task.er.dto.request.WebhookCreateRequest;
import com.uni.task.er.dto.response.WebhookResponse;
import com.uni.task.er.exception.custom.NotFoundException;
import com.uni.task.er.mapper.WebhookMapper;
import com.uni.task.er.model.Task;
import com.uni.task.er.model.User;
import com.uni.task.er.model.Webhook;
import com.uni.task.er.repository.UserRepository;
import com.uni.task.er.repository.WebhookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WebhookService {
    private final RestTemplate restTemplate = new RestTemplate();

    private final WebhookRepository webhookRepository;
    private final UserRepository userRepository;

    @Autowired
    public WebhookService(WebhookRepository webhookRepository, UserRepository userRepository) {
        this.webhookRepository = webhookRepository;
        this.userRepository = userRepository;
    }

    public WebhookResponse create(WebhookCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new NotFoundException("User not found"));
        Webhook webhook = WebhookMapper.toModel(request, user);
        Webhook saved = webhookRepository.save(webhook);
        return WebhookMapper.toResponse(saved);
    }

    public WebhookResponse getById(Long id) {
        Webhook webhook = webhookRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Webhook not found"));
        return WebhookMapper.toResponse(webhook);
    }

    public List<WebhookResponse> getByUserId(Long userId) {
        Boolean existUser = userRepository.existsByIdAndDeletedFalse(userId);

        if(!existUser) {
            throw new NotFoundException("User not found");
        }

        return webhookRepository.findByUserId(userId).stream()
            .map(WebhookMapper::toResponse)
            .collect(Collectors.toList());
    }

    public void delete(Long id) {
        webhookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Webhook not found"));

        webhookRepository.deleteById(id);
    }

    public void sendMessageByUser(User user, String message) {
        List<Webhook> webhooks = webhookRepository.findByUserId(user.getId());

        if (webhooks.isEmpty()) return;

        for (Webhook webhook : webhooks) {
            sendMessage(webhook.getUrl(), message);
        }
    }

    public void sendMessage(String webhookUrl, String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payload = new HashMap<>();
        payload.put("content", message);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(payload, headers);

        restTemplate.postForEntity(webhookUrl, request, String.class);
    }
} 