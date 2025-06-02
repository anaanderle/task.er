package com.uni.task.er.mapper;

import com.uni.task.er.dto.request.TaskCreateRequest;
import com.uni.task.er.dto.request.TaskUpdateRequest;
import com.uni.task.er.dto.request.WebhookCreateRequest;
import com.uni.task.er.dto.response.TaskResponse;
import com.uni.task.er.dto.response.WebhookResponse;
import com.uni.task.er.model.Task;
import com.uni.task.er.model.User;
import com.uni.task.er.model.Webhook;

public class WebhookMapper {
    public static Webhook toModel(WebhookCreateRequest request, User user) {
        return new Webhook(request.getDescription(), request.getUrl(), user);
    }

    public static WebhookResponse toResponse(Webhook webhook) {
        return new WebhookResponse(webhook.getId(), webhook.getDescription(), webhook.getUrl(), webhook.getUser().getId());
    }
}