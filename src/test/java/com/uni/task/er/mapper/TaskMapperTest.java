package com.uni.task.er.mapper;

import com.uni.task.er.dto.request.TaskCreateRequest;
import com.uni.task.er.dto.response.TaskResponse;
import com.uni.task.er.model.Task;
import com.uni.task.er.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TaskMapperTest {


    @Test
    void taskMapper_shouldConvertCreateRequestToTask() {
        User user = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");
        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("Tarefa Teste");
        request.setDescription("Descrição");
        request.setStatus("PENDING");

        Task task = TaskMapper.toModel(request, user);

        assertThat(task.getTitle()).isEqualTo("Tarefa Teste");
        assertThat(task.getDescription()).isEqualTo("Descrição");
        assertThat(task.getStatus()).isEqualTo("PENDING");
        assertThat(task.getUser()).isEqualTo(user);
    }

    @Test
    void taskMapper_shouldConvertTaskToResponse() {
        User user = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");
        user.setId(1L);
        Task task = new Task("Tarefa Teste", "Descrição", "PENDING", user);
        task.setId(1L);

        TaskResponse response = TaskMapper.toResponse(task);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getTitle()).isEqualTo("Tarefa Teste");
        assertThat(response.getDescription()).isEqualTo("Descrição");
        assertThat(response.getStatus()).isEqualTo("PENDING");
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getUserName()).isEqualTo("João");
    }
}