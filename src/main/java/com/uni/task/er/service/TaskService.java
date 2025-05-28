package com.uni.task.er.service;

import com.uni.task.er.dto.request.TaskCreateRequest;
import com.uni.task.er.dto.request.TaskUpdateRequest;
import com.uni.task.er.dto.response.TaskResponse;
import com.uni.task.er.model.Task;
import com.uni.task.er.model.User;
import com.uni.task.er.repository.TaskRepository;
import com.uni.task.er.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public TaskResponse create(TaskCreateRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        User user = userRepository.findById(request.getUserId()).orElse(null);
        task.setUser(user);
        Task saved = taskRepository.save(task);
        return toResponse(saved);
    }

    public TaskResponse getById(Long id) {
        Task task = taskRepository.findById(id).orElse(null);
        return toResponse(task);
    }

    public List<TaskResponse> getByUserId(Long userId) {
        return taskRepository.findByUserId(userId).stream().map(this::toResponse).collect(Collectors.toList());
    }

    public TaskResponse update(Long id, TaskUpdateRequest request) {
        Optional<Task> optionalTask = taskRepository.findById(id);
        if (optionalTask.isEmpty()) return null;
        Task task = optionalTask.get();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus());
        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId()).orElse(null);
            task.setUser(user);
        }
        Task saved = taskRepository.save(task);
        return toResponse(saved);
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }

    private TaskResponse toResponse(Task task) {
        if (task == null) return null;
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