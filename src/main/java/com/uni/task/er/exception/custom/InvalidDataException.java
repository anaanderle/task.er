package com.uni.task.er.exception.custom;

import lombok.Getter;

@Getter
public class InvalidDataException extends RuntimeException {
    private final String error = "INVALID_DATA";

    public InvalidDataException(String message) {
        super(message);
    }
}
