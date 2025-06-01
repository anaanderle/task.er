package com.uni.task.er.exception.custom;

import lombok.Getter;

@Getter
public class UnauthorizedException extends RuntimeException {
    private final String error = "UNAUTHORIZED";

    public UnauthorizedException(String message) {
        super(message);
    }
}
