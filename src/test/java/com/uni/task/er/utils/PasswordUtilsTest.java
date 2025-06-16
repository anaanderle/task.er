package com.uni.task.er.utils;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class PasswordUtilsTest {

    @Test
    void passwordUtils_shouldHashAndCheckPassword() {
        String plainPassword = "password123";

        String hashedPassword = PasswordUtils.hashPassword(plainPassword);
        boolean isValid = PasswordUtils.checkPassword(plainPassword, hashedPassword);
        boolean isInvalid = PasswordUtils.checkPassword("wrongPassword", hashedPassword);

        assertThat(hashedPassword).isNotEqualTo(plainPassword);
        assertThat(isValid).isTrue();
        assertThat(isInvalid).isFalse();
    }
}