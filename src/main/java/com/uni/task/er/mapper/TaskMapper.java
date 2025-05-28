package com.uni.task.er.mapper;

import com.uni.task.er.dto.request.TaskCreateRequest;
import com.uni.task.er.dto.request.TaskUpdateRequest;
import com.uni.task.er.dto.response.TaskResponse;
import com.uni.task.er.model.Task;
import com.uni.task.er.model.User;

public class TaskMapper {
    public static Task toModel(TaskCreateRequest request, User user) {
        return new Task(request.getTitle(), request.getDescription(), request.getStatus(), user);
    }

    public static Task toModel(TaskUpdateRequest request, Task task) {
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        return task;
    }

    public static TaskResponse toResponse(Task task) {
        TaskResponse response = new TaskResponse();
        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setStatus(task.getStatus());
        if (task.getUser() != null) {
            response.setUserId(task.getUser().getId());
            response.setUserName(task.getUser().getName());
        }
        return response;
    }
}