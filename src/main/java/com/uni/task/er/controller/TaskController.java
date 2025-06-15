package com.uni.task.er.controller;

import com.uni.task.er.dto.request.TaskCreateRequest;
import com.uni.task.er.dto.request.TaskUpdateRequest;
import com.uni.task.er.dto.response.TaskResponse;
import com.uni.task.er.model.User; // Import User
import com.uni.task.er.service.TaskService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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

    @PostMapping("/google")
    public ResponseEntity<TaskResponse> createTaskWithGoogleCalendar(
            @RequestBody TaskCreateRequest request,
            @RequestAttribute("user") User user) {
        if (user == null || user.getId() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não autenticado ou ID do usuário não encontrado.");
        }
        request.setUserId(user.getId());
        try {
            TaskResponse taskResponse = taskService.create(request);
            return ResponseEntity.ok(taskResponse);
        } catch (Exception e) {
            if (e.getMessage() != null && e.getMessage().contains("Usuário não autenticado com o Google")) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage(), e);
            }
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao criar task: " + e.getMessage(), e);
        }
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