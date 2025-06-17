package com.uni.task.er.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.uni.task.er.dto.request.UserCreateRequest;
import com.uni.task.er.model.User;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void userController_shouldCreateUserViaPost() throws Exception {
        UserCreateRequest request = new UserCreateRequest();
        request.setName("João");
        request.setEmail("joao@test.com");
        request.setPassword("password123");
        request.setConfirmPassword("password123");

        User user = new User();
        user.setId(1L);
        user.setName("João");
        user.setEmail("joao@test.com");
        user.setPassword("senhaSegura123");
        user.setDeleted(false);

        when(userRepository.save(any())).thenReturn(user);

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("João"))
                .andExpect(jsonPath("$.email").value("joao@test.com"));
    }

    @Test
    void userController_shouldGetUserById() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setName("João");
        user.setEmail("joao@test.com");
        user.setPassword("senhaSegura123");
        user.setDeleted(false);

        String fakeToken = "Bearer fake.jwt.token";

        when(authService.validateToken(fakeToken)).thenReturn(user);
        when(userRepository.findByIdAndDeletedFalse(1)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/1")
                        .header("Authorization", fakeToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("João"));
    }
}
