package com.uni.task.er.mapper;

import com.uni.task.er.dto.request.UserCreateRequest;
import com.uni.task.er.dto.request.UserUpdateRequest;
import com.uni.task.er.dto.response.UserResponse;
import com.uni.task.er.model.User;

public class UserMapper {

    public static User toModel(UserCreateRequest request) {
        return new User(request.getName(), request.getBirthDate(), request.getCellphone(), request.getEmail(), request.getPassword());
    }

    public static User toModel(UserUpdateRequest request, User user) {
        user.setName(request.getName());
        user.setBirthDate(request.getBirthDate());
        user.setCellphone(request.getCellphone());
        user.setEmail(request.getEmail());
        return user;
    }

    public static UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getBirthDate(), user.getCellphone(), user.getEmail());
    }
}
