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
    private final WebhookService webhookService;

    @Autowired
    private GoogleCalendarService googleCalendarService;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository, WebhookService webhookService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.webhookService = webhookService;
    }

    public TaskResponse create(TaskCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
            .orElseThrow(() -> new NotFoundException("User not found"));
        Task task = TaskMapper.toModel(request, user);
        Task saved = taskRepository.save(task);

        // Integrar com o Google Calendar
        // Verifica se o usuário autorizou o Google Calendar
        // Esta é uma verificação simplificada. Em um app real, você pode querer 
        // verificar se um token válido existe para o usuário no GoogleCalendarService.
        if (user.getId() != null) { // Adapte esta condição conforme sua lógica de autorização
            try {
                googleCalendarService.criarEvento(
                    user.getId().toString(), // Passa o ID do usuário
                    request.getTitle(),
                    request.getDescription(),
                    request.getStartDate(),
                    request.getEndDate()
                );
            } catch (Exception e) {
                // Trate o erro conforme sua necessidade, por exemplo, logar ou notificar o usuário
                System.err.println("Erro ao criar evento no Google Calendar: " + e.getMessage());
                // e.printStackTrace(); // Descomente para debug mais detalhado
            }
        }

        webhookService.sendMessageByUser(user, "Task " + saved.getTitle() + " criada!");

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


        webhookService.sendMessageByUser(task.getUser(), "Task " + saved.getTitle() + " editada!");

        return TaskMapper.toResponse(saved);
    }

    public void delete(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found"));

        taskRepository.deleteById(id);

        webhookService.sendMessageByUser(task.getUser(), "Task " + task.getTitle() + " deletada!");
    }
}