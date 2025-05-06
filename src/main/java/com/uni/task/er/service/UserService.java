package com.uni.task.er.service;

import com.uni.task.er.dto.request.UserCreateRequest;
import com.uni.task.er.dto.request.UserUpdateRequest;
import com.uni.task.er.dto.response.UserResponse;
import com.uni.task.er.exception.custom.NotFoundException;
import com.uni.task.er.mapper.UserMapper;
import com.uni.task.er.model.User;
import com.uni.task.er.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private Optional<User> getEntityById(long id) {
        return userRepository.findById(id);
    }

    private Optional<User> getNotDeletedEntityById(long id) {
        return userRepository.findByIdAndDeletedFalse(id);
    }

    public UserResponse getById(long id) {
        User user = getNotDeletedEntityById(id).orElseThrow(() -> new NotFoundException("User not found"));

        return UserMapper.toResponse(user);
    }

    @Transactional
    public UserResponse create(UserCreateRequest request) {
        User user = UserMapper.toModel(request);

        return UserMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public UserResponse update(long id, UserUpdateRequest request) {
        User user = getNotDeletedEntityById(id).orElseThrow(() -> new NotFoundException("User not found"));

        User updatedUser = UserMapper.toModel(request, user);

        return UserMapper.toResponse(userRepository.save(updatedUser));
    }

    @Transactional
    public void delete(long id) {
        User user = getNotDeletedEntityById(id).orElseThrow(() -> new NotFoundException("User not found"));

        user.setDeleted(true);

        userRepository.save(user);
    }
}
