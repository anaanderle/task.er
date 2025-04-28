package com.uni.task.er.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskerException {
    private String error;
    private String message;

    public TaskerException(String error, String message) {
        this.error = error;
        this.message = message;
    }
}
