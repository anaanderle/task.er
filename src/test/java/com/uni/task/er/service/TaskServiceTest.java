package com.uni.task.er.service;

import com.uni.task.er.dto.request.TaskCreateRequest;
import com.uni.task.er.dto.request.TaskUpdateRequest;
import com.uni.task.er.dto.response.TaskResponse;
import com.uni.task.er.exception.custom.NotFoundException;
import com.uni.task.er.model.Task;
import com.uni.task.er.model.User;
import com.uni.task.er.repository.TaskRepository;
import com.uni.task.er.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class TaskServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private WebhookService webhookService;

    @Test
    void taskService_shouldCreateTaskSuccessfully() {
        TaskService taskService = new TaskService(taskRepository, userRepository, webhookService);

        User user = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");
        user.setId(1L);

        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("Nova Tarefa");
        request.setDescription("Descrição");
        request.setStatus("PENDING");
        request.setUserId(1L);

        Task savedTask = new Task("Nova Tarefa", "Descrição", "PENDING", user);
        savedTask.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(taskRepository.save(any(Task.class))).thenReturn(savedTask);

        TaskResponse response = taskService.create(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Nova Tarefa");
        assertThat(response.getUserId()).isEqualTo(1L);
        verify(taskRepository).save(any(Task.class));
        verify(webhookService).sendMessageByUser(eq(user), contains("criada"));
    }

    @Test
    void taskService_shouldThrowExceptionWhenUserNotFound() {
        TaskService taskService = new TaskService(taskRepository, userRepository, webhookService);

        TaskCreateRequest request = new TaskCreateRequest();
        request.setUserId(999L);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.create(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void taskService_shouldGetTaskByIdSuccessfully() {

        TaskService taskService = new TaskService(taskRepository, userRepository, webhookService);
        Long taskId = 1L;
        
        User user = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");
        user.setId(1L);
        
        Task task = new Task("Tarefa Teste", "Descrição", "PENDING", user);
        task.setId(taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        TaskResponse response = taskService.getById(taskId);

        assertThat(response.getId()).isEqualTo(taskId);
        assertThat(response.getTitle()).isEqualTo("Tarefa Teste");
        assertThat(response.getDescription()).isEqualTo("Descrição");
        assertThat(response.getStatus()).isEqualTo("PENDING");
        assertThat(response.getUserId()).isEqualTo(1L);
        verify(taskRepository).findById(taskId);
    }

    @Test
    void taskService_shouldThrowExceptionWhenTaskNotFoundById() {

        TaskService taskService = new TaskService(taskRepository, userRepository, webhookService);
        Long taskId = 999L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.getById(taskId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Task not found");
        
        verify(taskRepository).findById(taskId);
    }


    @Test
    void taskService_shouldGetTasksByUserIdSuccessfully() {

        TaskService taskService = new TaskService(taskRepository, userRepository, webhookService);
        Long userId = 1L;
        
        User user = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");
        user.setId(userId);
        
        Task task1 = new Task("Tarefa 1", "Descrição 1", "PENDING", user);
        task1.setId(1L);
        
        Task task2 = new Task("Tarefa 2", "Descrição 2", "IN_PROGRESS", user);
        task2.setId(2L);
        
        List<Task> tasks = Arrays.asList(task1, task2);

        when(taskRepository.findByUserId(userId)).thenReturn(tasks);

        List<TaskResponse> responses = taskService.getByUserId(userId);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getTitle()).isEqualTo("Tarefa 1");
        assertThat(responses.get(1).getTitle()).isEqualTo("Tarefa 2");
        assertThat(responses.get(0).getUserId()).isEqualTo(userId);
        assertThat(responses.get(1).getUserId()).isEqualTo(userId);
        verify(taskRepository).findByUserId(userId);
    }

    @Test
    void taskService_shouldReturnEmptyListWhenUserHasNoTasks() {

        TaskService taskService = new TaskService(taskRepository, userRepository, webhookService);
        Long userId = 1L;

        when(taskRepository.findByUserId(userId)).thenReturn(Arrays.asList());

        List<TaskResponse> responses = taskService.getByUserId(userId);

        assertThat(responses).isEmpty();
        verify(taskRepository).findByUserId(userId);
    }

    @Test
    void taskService_shouldThrowExceptionWhenUserIdIsNull() {

        TaskService taskService = new TaskService(taskRepository, userRepository, webhookService);

        assertThatThrownBy(() -> taskService.getByUserId(null))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User ID is required");
        
        verify(taskRepository, never()).findByUserId(any());
    }

    @Test
    void taskService_shouldUpdateTaskSuccessfully() {

        TaskService taskService = new TaskService(taskRepository, userRepository, webhookService);
        Long taskId = 1L;
        
        User user = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");
        user.setId(1L);
        
        Task existingTask = new Task("Tarefa Original", "Descrição Original", "PENDING", user);
        existingTask.setId(taskId);
        
        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("Tarefa Atualizada");
        request.setDescription("Descrição Atualizada");
        request.setStatus("IN_PROGRESS");
        
        Task updatedTask = new Task("Tarefa Atualizada", "Descrição Atualizada", "IN_PROGRESS", user);
        updatedTask.setId(taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        TaskResponse response = taskService.update(taskId, request);

        assertThat(response.getId()).isEqualTo(taskId);
        assertThat(response.getTitle()).isEqualTo("Tarefa Atualizada");
        assertThat(response.getDescription()).isEqualTo("Descrição Atualizada");
        assertThat(response.getStatus()).isEqualTo("IN_PROGRESS");
        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(any(Task.class));
        verify(webhookService).sendMessageByUser(eq(user), contains("editada"));
    }

    @Test
    void taskService_shouldUpdateTaskWithNewUserSuccessfully() {
        TaskService taskService = new TaskService(taskRepository, userRepository, webhookService);

        User originalUser = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");
        originalUser.setId(1L);

        User newUser = new User("Maria", LocalDate.of(1992, 2, 2), "11888888888", "maria@test.com", "pass");
        newUser.setId(2L);

        Task existingTask = new Task("Tarefa Original", "Descrição", "PENDING", originalUser);
        existingTask.setId(1L);

        Task updatedTask = new Task("Tarefa Atualizada", "Nova Descrição", "COMPLETED", newUser);
        updatedTask.setId(1L);

        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("Tarefa Atualizada");
        request.setDescription("Nova Descrição");
        request.setStatus("COMPLETED");
        request.setUserId(2L);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));
        when(userRepository.findById(2L)).thenReturn(Optional.of(newUser));
        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);

        TaskResponse response = taskService.update(1L, request);

        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("Tarefa Atualizada");
        assertThat(response.getDescription()).isEqualTo("Nova Descrição");
        assertThat(response.getStatus()).isEqualTo("COMPLETED");

        verify(taskRepository).findById(1L);
        verify(userRepository).findById(2L);
        verify(taskRepository).save(any(Task.class));

        verify(webhookService).sendMessageByUser(any(User.class), contains("editada"));
    }

    @Test
    void taskService_shouldThrowExceptionWhenUpdateTaskNotFound() {

        TaskService taskService = new TaskService(taskRepository, userRepository, webhookService);
        Long taskId = 999L;
        
        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("Tarefa Atualizada");

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.update(taskId, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Task not found");
        
        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).save(any(Task.class));
        verify(webhookService, never()).sendMessageByUser(any(), any());
    }

    @Test
    void taskService_shouldThrowExceptionWhenUpdateWithInvalidUserId() {

        TaskService taskService = new TaskService(taskRepository, userRepository, webhookService);
        Long taskId = 1L;
        Long invalidUserId = 999L;
        
        User user = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");
        user.setId(1L);
        
        Task existingTask = new Task("Tarefa Original", "Descrição Original", "PENDING", user);
        existingTask.setId(taskId);
        
        TaskUpdateRequest request = new TaskUpdateRequest();
        request.setTitle("Tarefa Atualizada");
        request.setUserId(invalidUserId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
        when(userRepository.findById(invalidUserId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.update(taskId, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found");
        
        verify(taskRepository).findById(taskId);
        verify(userRepository).findById(invalidUserId);
        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void taskService_shouldDeleteTaskSuccessfully() {

        TaskService taskService = new TaskService(taskRepository, userRepository, webhookService);
        Long taskId = 1L;
        
        User user = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");
        user.setId(1L);
        
        Task task = new Task("Tarefa para Deletar", "Descrição", "PENDING", user);
        task.setId(taskId);

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        taskService.delete(taskId);

        verify(taskRepository).findById(taskId);
        verify(taskRepository).deleteById(taskId);
        verify(webhookService).sendMessageByUser(eq(user), contains("deletada"));
    }

    @Test
    void taskService_shouldThrowExceptionWhenDeleteTaskNotFound() {

        TaskService taskService = new TaskService(taskRepository, userRepository, webhookService);
        Long taskId = 999L;

        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.delete(taskId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Task not found");
        
        verify(taskRepository).findById(taskId);
        verify(taskRepository, never()).deleteById(any());
        verify(webhookService, never()).sendMessageByUser(any(), any());
    }
}