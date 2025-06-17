package com.uni.task.er.repository;

import com.uni.task.er.model.User;
import com.uni.task.er.model.Webhook;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class WebhookRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebhookRepository webhookRepository;

    @Test
    void shouldSaveWebhookAndFindByUser() {
        User user = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");
        User savedUser = userRepository.save(user);

        Webhook webhook = new Webhook("Webhook Teste", "https://example.com", savedUser);

        Webhook savedWebhook = webhookRepository.save(webhook);
        List<Webhook> userWebhooks = webhookRepository.findByUserId(savedUser.getId());

        assertThat(savedWebhook.getId()).isNotNull();
        assertThat(userWebhooks).hasSize(1);
        assertThat(userWebhooks.getFirst().getDescription()).isEqualTo("Webhook Teste");
    }
}