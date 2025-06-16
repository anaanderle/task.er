package com.uni.task.er.service;

import com.uni.task.er.dto.request.TaskCreateRequest;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
}