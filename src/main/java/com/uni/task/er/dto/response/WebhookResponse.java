package com.uni.task.er.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class WebhookResponse {
    private Long id;
    private String description;
    private String url;
    private Long userId;
}