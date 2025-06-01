package com.uni.task.er.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private String status;
    private Long userId;
    private String userName;
} 