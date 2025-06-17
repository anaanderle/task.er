package com.uni.task.er.utils;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class JwtUtilsTest {

    @Test
    void shouldGenerateValidToken() {
        String username = "testuser";

        String token = JwtUtils.generateToken(username);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
        assertThat(token.split("\\.")).hasSize(3); // JWT deve ter 3 partes separadas por pontos
    }

    @Test
    void shouldGenerateTokensWithDifferentUsernamesAreDifferent() {
        String username1 = "user1";
        String username2 = "user2";

        String token1 = JwtUtils.generateToken(username1);
        String token2 = JwtUtils.generateToken(username2);

        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    void shouldGenerateTokensAtDifferentTimesAreDifferent() throws InterruptedException {
        String username = "testuser";

        String token1 = JwtUtils.generateToken(username);
        Thread.sleep(1000); // Espera 1 segundo para garantir timestamps diferentes
        String token2 = JwtUtils.generateToken(username);

        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    void shouldValidateValidTokenAndReturnCorrectUsername() {
        String username = "testuser123";

        String token = JwtUtils.generateToken(username);
        String extractedUsername = JwtUtils.validateTokenAndGetUsername(token);

        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    void shouldValidateTokenWithSpecialCharactersInUsername() {
        String username = "test.user+123@example.com";

        String token = JwtUtils.generateToken(username);
        String extractedUsername = JwtUtils.validateTokenAndGetUsername(token);

        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    void shouldThrowExceptionForInvalidToken() {
        String invalidToken = "invalid.token.here";

        assertThrows(MalformedJwtException.class, () -> {
            JwtUtils.validateTokenAndGetUsername(invalidToken);
        });
    }

    @Test
    void shouldThrowExceptionForMalformedToken() {
        String malformedToken = "not-a-jwt-token";

        assertThrows(MalformedJwtException.class, () -> {
            JwtUtils.validateTokenAndGetUsername(malformedToken);
        });
    }

    @Test
    void shouldThrowExceptionForTamperedToken() {
        String username = "testuser";
        String validToken = JwtUtils.generateToken(username);
        
        // Modifica o token para simular uma tentativa de adulteração
        String tamperedToken = validToken.substring(0, validToken.length() - 5) + "XXXXX";

        assertThrows(SignatureException.class, () -> {
            JwtUtils.validateTokenAndGetUsername(tamperedToken);
        });
    }

    @Test
    void shouldThrowExceptionForNullToken() {
        assertThrows(IllegalArgumentException.class, () -> {
            JwtUtils.validateTokenAndGetUsername(null);
        });
    }

    @Test
    void shouldThrowExceptionForEmptyToken() {
        assertThrows(IllegalArgumentException.class, () -> {
            JwtUtils.validateTokenAndGetUsername("");
        });
    }

    @Test
    void shouldHandleLongUsername() {
        String longUsername = "a".repeat(1000); // Username muito longo

        String token = JwtUtils.generateToken(longUsername);
        String extractedUsername = JwtUtils.validateTokenAndGetUsername(token);

        assertThat(extractedUsername).isEqualTo(longUsername);
    }

}
