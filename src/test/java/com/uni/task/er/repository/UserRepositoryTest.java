package com.uni.task.er.repository;

import com.uni.task.er.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserByEmail() {
        User user = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");

        User savedUser = userRepository.save(user);
        Optional<User> foundUser = userRepository.findByEmail("joao@test.com");

        assertThat(savedUser.getId()).isNotNull();
        assertThat(foundUser).isPresent();
        assertEquals("joao@test.com", foundUser.get().getEmail());
    }


    @Test
    void shouldFindUserByIdAndNotDeleted() {
        User user = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");
        User savedUser = userRepository.save(user);

        Optional<User> foundUser = userRepository.findByIdAndDeletedFalse(savedUser.getId());

        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getDeleted()).isFalse();
    }
}