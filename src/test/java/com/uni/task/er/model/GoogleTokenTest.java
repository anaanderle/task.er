package com.uni.task.er.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class GoogleTokenTest {

    @Test
    void shouldCreateGoogleTokenWithAllParameters() {
        String userId = "123";
        String accessToken = "access_token_123";
        String refreshToken = "refresh_token_123";
        Long expiresIn = 3600L;
        String tokenType = "Bearer";

        GoogleToken token = new GoogleToken(userId, accessToken, refreshToken, expiresIn, tokenType);

        assertEquals(userId, token.getUserId());
        assertEquals(accessToken, token.getAccessToken());
        assertEquals(refreshToken, token.getRefreshToken());
        assertEquals(expiresIn, token.getExpiresIn());
        assertEquals(tokenType, token.getTokenType());
        assertNull(token.getId()); // ID deve ser null antes de persistir
    }

    @Test
    void shouldCreateEmptyGoogleToken() {
        GoogleToken token = new GoogleToken();

        assertNull(token.getId());
        assertNull(token.getUserId());
        assertNull(token.getAccessToken());
        assertNull(token.getRefreshToken());
        assertNull(token.getExpiresIn());
        assertNull(token.getTokenType());
    }

    @Test
    void shouldSetAndGetAllFields() {
        GoogleToken token = new GoogleToken();
        Long id = 1L;
        String userId = "456";
        String accessToken = "new_access_token";
        String refreshToken = "new_refresh_token";
        Long expiresIn = 7200L;
        String tokenType = "Bearer";

        token.setId(id);
        token.setUserId(userId);
        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);
        token.setExpiresIn(expiresIn);
        token.setTokenType(tokenType);

        assertEquals(id, token.getId());
        assertEquals(userId, token.getUserId());
        assertEquals(accessToken, token.getAccessToken());
        assertEquals(refreshToken, token.getRefreshToken());
        assertEquals(expiresIn, token.getExpiresIn());
        assertEquals(tokenType, token.getTokenType());
    }

    @Test
    void shouldHandleNullValues() {
        GoogleToken token = new GoogleToken();

        token.setUserId(null);
        token.setAccessToken(null);
        token.setRefreshToken(null);
        token.setExpiresIn(null);
        token.setTokenType(null);

        assertNull(token.getUserId());
        assertNull(token.getAccessToken());
        assertNull(token.getRefreshToken());
        assertNull(token.getExpiresIn());
        assertNull(token.getTokenType());
    }

    @Test
    void shouldUpdateExistingToken() {
        GoogleToken token = new GoogleToken("123", "old_access", "old_refresh", 3600L, "Bearer");
        
        String newAccessToken = "new_access_token";
        String newRefreshToken = "new_refresh_token";
        Long newExpiresIn = 7200L;

        token.setAccessToken(newAccessToken);
        token.setRefreshToken(newRefreshToken);
        token.setExpiresIn(newExpiresIn);

        assertEquals("123", token.getUserId()); // userId deve permanecer o mesmo
        assertEquals(newAccessToken, token.getAccessToken());
        assertEquals(newRefreshToken, token.getRefreshToken());
        assertEquals(newExpiresIn, token.getExpiresIn());
        assertEquals("Bearer", token.getTokenType());
    }

    @Test
    void shouldCreateTokenWithLongExpirationTime() {
        String userId = "789";
        Long longExpiresIn = 86400L; // 24 horas
        
        GoogleToken token = new GoogleToken(userId, "access", "refresh", longExpiresIn, "Bearer");

        assertEquals(longExpiresIn, token.getExpiresIn());
        assertTrue(token.getExpiresIn() > 3600L); // Verifica se é maior que 1 hora
    }
}
