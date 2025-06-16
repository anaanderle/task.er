package com.uni.task.er.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class UserTest {

    @Test
    void shouldCreateValidUser() {
        String name = "João Silva";
        LocalDate birthday = LocalDate.of(1990, 1, 1);
        String cellphone = "11999999999";
        String email = "joao@test.com";
        String password = "password123";

        User user = new User(name, birthday, cellphone, email, password);

        assertEquals(name, user.getName());
        assertEquals(birthday, user.getBirthday());
        assertEquals(cellphone, user.getCellphone());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(false, user.getDeleted());
    }
}