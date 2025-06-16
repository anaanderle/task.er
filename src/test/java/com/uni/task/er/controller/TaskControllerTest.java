package com.uni.task.er.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uni.task.er.dto.request.TaskCreateRequest;
import com.uni.task.er.model.Task;
import com.uni.task.er.model.User;
import com.uni.task.er.repository.TaskRepository;
import com.uni.task.er.repository.UserRepository;
import com.uni.task.er.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TaskRepository taskRepository;

    @Test
    void taskController_shouldCreateTaskViaPost() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("João");
        user.setEmail("joao@test.com");
        user.setPassword("senhaSegura123");
        userRepository.save(user);

        Task task = new Task();
        task.setId(1L);
        task.setTitle("Nova Tarefa");
        task.setDescription("Descrição");
        task.setStatus("PENDING");
        task.setUser(user);

        TaskCreateRequest request = new TaskCreateRequest();
        request.setTitle("Nova Tarefa");
        request.setDescription("Descrição");
        request.setStatus("PENDING");
        request.setUserId(1L);

        String fakeToken = "Bearer fake.jwt.token";

        when(authService.validateToken(fakeToken)).thenReturn(user);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(taskRepository.save(any())).thenReturn(task);

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", fakeToken)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Nova Tarefa"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
}