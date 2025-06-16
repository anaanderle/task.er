package com.uni.task.er.service;

import com.uni.task.er.dto.request.UserCreateRequest;
import com.uni.task.er.dto.response.UserResponse;
import com.uni.task.er.model.User;
import com.uni.task.er.repository.UserRepository;
import com.uni.task.er.utils.PasswordUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void userService_shouldCreateUserSuccessfully() {
        UserService userService = new UserService(userRepository);
        UserCreateRequest request = new UserCreateRequest();
        request.setName("João");
        request.setBirthday(LocalDate.of(1990, 1, 1));
        request.setCellphone("11999999999");
        request.setEmail("joao@test.com");
        request.setPassword("password123");
        request.setConfirmPassword("password123");

        User savedUser = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "hashedPassword");
        savedUser.setId(1L);

        try (MockedStatic<PasswordUtils> passwordUtils = mockStatic(PasswordUtils.class)) {
            passwordUtils.when(() -> PasswordUtils.hashPassword("password123")).thenReturn("hashedPassword");
            when(userRepository.save(any(User.class))).thenReturn(savedUser);

            UserResponse response = userService.create(request);

            assertThat(response.getId()).isEqualTo(1L);
            assertThat(response.getName()).isEqualTo("João");
            assertThat(response.getEmail()).isEqualTo("joao@test.com");
            verify(userRepository).save(any(User.class));
        }
    }

    @Test
    void userService_shouldThrowExceptionWhenPasswordsDontMatch() {
        UserService userService = new UserService(userRepository);
        UserCreateRequest request = new UserCreateRequest();
        request.setPassword("password123");
        request.setConfirmPassword("differentPassword");

        assertThatThrownBy(() -> userService.create(request))
                .isInstanceOf(Exception.class);
    }
}