package com.uni.task.er.exception.handler;

import com.uni.task.er.exception.TaskerException;
import com.uni.task.er.exception.custom.InvalidDataException;
import com.uni.task.er.exception.custom.NotFoundException;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Hidden
@RestControllerAdvice
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

    @ExceptionHandler(InvalidDataException.class)
    public ResponseEntity<TaskerException> handleInvalidDataException(InvalidDataException ex) {
        TaskerException taskerException = new TaskerException(ex.getError(), ex.getMessage());
        return new ResponseEntity<>(taskerException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<TaskerException> handleConstraintViolationException(ConstraintViolationException ex) {
        String[] details = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + " " + v.getMessage()).toArray(String[]::new);

        TaskerException taskerException = new TaskerException("CONSTRAINT_VIOLATION", details);
        return new ResponseEntity<>(taskerException, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<TaskerException> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        TaskerException taskerException = new TaskerException("CONSTRAINT_VIOLATION", ex.getMessage());
        return new ResponseEntity<>(taskerException, HttpStatus.BAD_REQUEST);
    }

}
