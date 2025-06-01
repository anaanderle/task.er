package com.uni.task.er.controller;

import com.uni.task.er.dto.request.TaskCreateRequest;
import com.uni.task.er.dto.request.TaskUpdateRequest;
import com.uni.task.er.dto.response.TaskResponse;
import com.uni.task.er.service.TaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Tarefas")
@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public TaskResponse create(@RequestBody TaskCreateRequest request) {
        return taskService.create(request);
    }

    @GetMapping("/{id}")
    public TaskResponse getById(@PathVariable Long id) {
        return taskService.getById(id);
    }

    @GetMapping
    public List<TaskResponse> getByUser(@RequestParam(value = "assignedTo", required = false) Long userId) {
        if (userId != null) {
            return taskService.getByUserId(userId);
        }
        return List.of();
    }

    @PutMapping("/{id}")
    public TaskResponse update(@PathVariable Long id, @RequestBody TaskUpdateRequest request) {
        return taskService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        taskService.delete(id);
    }
} 