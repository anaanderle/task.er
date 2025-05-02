package com.uni.task.er.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TaskerException {
    private String error;
    private String message;
    private String[] details;

    public TaskerException(String error, String message) {
        this.error = error;
        this.message = message;
    }

    public TaskerException(String error, String[] details) {
        this.error = error;
        this.details = details;
    }
}
