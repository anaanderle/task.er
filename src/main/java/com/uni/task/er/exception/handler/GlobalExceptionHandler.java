package com.uni.task.er.exception.handler;

import com.uni.task.er.exception.TaskerException;
import com.uni.task.er.exception.custom.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<TaskerException> handleNoResourceFoundException() {
        TaskerException taskerException = new TaskerException("NOT_FOUND", "Resource not found");
        return new ResponseEntity<>(taskerException, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<TaskerException> handleNotFoundException(NotFoundException ex) {
        TaskerException taskerException = new TaskerException(ex.getError(), ex.getMessage());
        return new ResponseEntity<>(taskerException, HttpStatus.NOT_FOUND);
    }
}
