package com.uni.task.er.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
class WebhookTest {

    @Test
    @DisplayName("Deve criar um webhook válido")
    void shouldCreateValidWebhook() {
        User user = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");
        String description = "Webhook Teste";
        String url = "https://example.com/webhook";

        Webhook webhook = new Webhook(description, url, user);

        assertEquals(description, webhook.getDescription());
        assertEquals(url, webhook.getUrl());
        assertEquals(user, webhook.getUser());
    }
}