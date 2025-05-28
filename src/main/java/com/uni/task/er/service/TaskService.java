package com.uni.task.er.service;

import com.uni.task.er.dto.request.TaskCreateRequest;
import com.uni.task.er.dto.request.TaskUpdateRequest;
import com.uni.task.er.dto.response.TaskResponse;
import com.uni.task.er.exception.custom.NotFoundException;
import com.uni.task.er.mapper.TaskMapper;
import com.uni.task.er.model.Task;
import com.uni.task.er.model.User;
import com.uni.task.er.repository.TaskRepository;
import com.uni.task.er.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new NotFoundException("User not found"));
        Task task = TaskMapper.toModel(request, user);
        Task saved = taskRepository.save(task);
        return TaskMapper.toResponse(saved);
    }

    public TaskResponse getById(Long id) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Task not found"));
        return TaskMapper.toResponse(task);
    }

    public List<TaskResponse> getByUserId(Long userId) {
        if (userId == null) {
            throw new NotFoundException("User ID is required");
        }
        return taskRepository.findByUserId(userId).stream()
            .map(TaskMapper::toResponse)
            .collect(Collectors.toList());
    }

    public TaskResponse update(Long id, TaskUpdateRequest request) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Task not found"));
        Task updatedTask = TaskMapper.toModel(request, task);
        if (request.getUserId() != null) {
            User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found"));
            updatedTask.setUser(user);
        }
        Task saved = taskRepository.save(updatedTask);
        return TaskMapper.toResponse(saved);
    }

    public void delete(Long id) {
        taskRepository.deleteById(id);
    }
} 