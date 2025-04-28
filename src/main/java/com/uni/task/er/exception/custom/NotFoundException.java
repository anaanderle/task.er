package com.uni.task.er.exception.custom;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    private final String error = "NOT_FOUND";

    public NotFoundException(String message) {
        super(message);
    }
}
