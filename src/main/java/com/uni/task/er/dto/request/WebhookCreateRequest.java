package com.uni.task.er.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebhookCreateRequest {
    private String description;
    private String url;
    private Long userId;
}