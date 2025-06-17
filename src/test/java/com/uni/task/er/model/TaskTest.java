package com.uni.task.er.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class TaskTest {

    @Test
    void shouldCreateValidTask() {
        User user = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");
        String title = "Tarefa Teste";
        String description = "Descrição da tarefa";
        String status = "PENDING";

        Task task = new Task(title, description, status, user);

        assertEquals(title, task.getTitle());
        assertEquals(description, task.getDescription());
        assertEquals(status, task.getStatus());
        assertEquals(user, task.getUser());
    }
}