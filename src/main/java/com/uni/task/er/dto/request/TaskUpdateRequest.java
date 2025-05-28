package com.uni.task.er.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskUpdateRequest {
    private String title;
    private String description;
    private String status;
    private Long userId;
} 