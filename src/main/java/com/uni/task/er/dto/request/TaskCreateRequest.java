package com.uni.task.er.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskCreateRequest {
    private String title;
    private String description;
    private String status;
    private Long userId;
    private java.util.Date startDate;
    private java.util.Date endDate;
}