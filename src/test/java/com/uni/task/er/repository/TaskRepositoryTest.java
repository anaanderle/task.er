package com.uni.task.er.repository;

import com.uni.task.er.model.Task;
import com.uni.task.er.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    void shouldSaveTaskAndFindByUser() {
        User user = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");
        User savedUser = userRepository.save(user);

        Task task = new Task("Tarefa Teste", "Descrição", "PENDING", savedUser);

        Task savedTask = taskRepository.save(task);
        List<Task> userTasks = taskRepository.findByUserId(savedUser.getId());

        assertThat(savedTask.getId()).isNotNull();
        assertThat(userTasks).hasSize(1);
        assertThat(userTasks.getFirst().getTitle()).isEqualTo("Tarefa Teste");
    }
}