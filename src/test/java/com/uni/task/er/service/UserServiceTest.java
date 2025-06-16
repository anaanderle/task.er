package com.uni.task.er.service;

import com.uni.task.er.dto.request.UserCreateRequest;
import com.uni.task.er.dto.request.UserUpdateRequest;
import com.uni.task.er.dto.response.UserResponse;
import com.uni.task.er.exception.custom.NotFoundException;
import com.uni.task.er.model.User;
import com.uni.task.er.repository.UserRepository;
import com.uni.task.er.utils.PasswordUtils;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

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

    @Test
    void userService_shouldUpdateUserSuccessfully() {

        UserService userService = new UserService(userRepository);
        long userId = 1L;
        
        UserUpdateRequest request = new UserUpdateRequest();
        request.setName("João Atualizado");
        request.setBirthday(LocalDate.of(1990, 1, 1));
        request.setCellphone("11888888888");
        request.setEmail("joao.updated@test.com");

        User existingUser = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "hashedPassword");
        existingUser.setId(userId);
        existingUser.setDeleted(false);

        User updatedUser = new User("João Atualizado", LocalDate.of(1990, 1, 1), "11888888888", "joao.updated@test.com", "hashedPassword");
        updatedUser.setId(userId);
        updatedUser.setDeleted(false);

        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserResponse response = userService.update(userId, request);

        assertThat(response.getId()).isEqualTo(userId);
        assertThat(response.getName()).isEqualTo("João Atualizado");
        assertThat(response.getEmail()).isEqualTo("joao.updated@test.com");
        assertThat(response.getCellphone()).isEqualTo("11888888888");
        verify(userRepository).findByIdAndDeletedFalse(userId);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void userService_shouldThrowExceptionWhenUpdateUserNotFound() {

        UserService userService = new UserService(userRepository);
        long userId = 999L;
        
        UserUpdateRequest request = new UserUpdateRequest();
        request.setName("João Atualizado");
        request.setEmail("joao.updated@test.com");

        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(userId, request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found");
        
        verify(userRepository).findByIdAndDeletedFalse(userId);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void userService_shouldDeleteUserSuccessfully() {

        UserService userService = new UserService(userRepository);
        long userId = 1L;

        User existingUser = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "hashedPassword");
        existingUser.setId(userId);
        existingUser.setDeleted(false);

        User deletedUser = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "hashedPassword");
        deletedUser.setId(userId);
        deletedUser.setDeleted(true);

        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(any(User.class))).thenReturn(deletedUser);

        userService.delete(userId);

        verify(userRepository).findByIdAndDeletedFalse(userId);
        verify(userRepository).save(argThat(user -> user.getDeleted() == true));
    }

    @Test
    void userService_shouldThrowExceptionWhenDeleteUserNotFound() {

        UserService userService = new UserService(userRepository);
        long userId = 999L;

        when(userRepository.findByIdAndDeletedFalse(userId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.delete(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found");
        
        verify(userRepository).findByIdAndDeletedFalse(userId);
        verify(userRepository, never()).save(any(User.class));
    }
}