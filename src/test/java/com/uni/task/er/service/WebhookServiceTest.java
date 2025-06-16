package com.uni.task.er.service;

import com.uni.task.er.dto.request.WebhookCreateRequest;
import com.uni.task.er.dto.response.WebhookResponse;
import com.uni.task.er.exception.custom.NotFoundException;
import com.uni.task.er.model.User;
import com.uni.task.er.model.Webhook;
import com.uni.task.er.repository.UserRepository;
import com.uni.task.er.repository.WebhookRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
class WebhookServiceTest {

    @Mock
    private WebhookRepository webhookRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void webhookService_shouldCreateWebhookSuccessfully() {

        WebhookService webhookService = new WebhookService(webhookRepository, userRepository);
        
        User user = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");
        user.setId(1L);
        
        WebhookCreateRequest request = new WebhookCreateRequest();
        request.setDescription("Webhook Discord");
        request.setUrl("https://discord.com/api/webhooks/123456");
        request.setUserId(1L);
        
        Webhook savedWebhook = new Webhook("Webhook Discord", "https://discord.com/api/webhooks/123456", user);
        savedWebhook.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(webhookRepository.save(any(Webhook.class))).thenReturn(savedWebhook);

        WebhookResponse response = webhookService.create(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getDescription()).isEqualTo("Webhook Discord");
        assertThat(response.getUrl()).isEqualTo("https://discord.com/api/webhooks/123456");
        assertThat(response.getUserId()).isEqualTo(1L);
        verify(userRepository).findById(1L);
        verify(webhookRepository).save(any(Webhook.class));
    }

    @Test
    void webhookService_shouldThrowExceptionWhenCreateWithUserNotFound() {

        WebhookService webhookService = new WebhookService(webhookRepository, userRepository);
        
        WebhookCreateRequest request = new WebhookCreateRequest();
        request.setUserId(999L);

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> webhookService.create(request))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found");
        
        verify(userRepository).findById(999L);
        verify(webhookRepository, never()).save(any(Webhook.class));
    }

    @Test
    void webhookService_shouldGetWebhookByIdSuccessfully() {

        WebhookService webhookService = new WebhookService(webhookRepository, userRepository);
        Long webhookId = 1L;
        
        User user = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");
        user.setId(1L);
        
        Webhook webhook = new Webhook("Webhook Teste", "https://test.com/webhook", user);
        webhook.setId(webhookId);

        when(webhookRepository.findById(webhookId)).thenReturn(Optional.of(webhook));

        WebhookResponse response = webhookService.getById(webhookId);

        assertThat(response.getId()).isEqualTo(webhookId);
        assertThat(response.getDescription()).isEqualTo("Webhook Teste");
        assertThat(response.getUrl()).isEqualTo("https://test.com/webhook");
        assertThat(response.getUserId()).isEqualTo(1L);
        verify(webhookRepository).findById(webhookId);
    }

    @Test
    void webhookService_shouldThrowExceptionWhenWebhookNotFoundById() {

        WebhookService webhookService = new WebhookService(webhookRepository, userRepository);
        Long webhookId = 999L;

        when(webhookRepository.findById(webhookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> webhookService.getById(webhookId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Webhook not found");
        
        verify(webhookRepository).findById(webhookId);
    }

    @Test
    void webhookService_shouldGetWebhooksByUserIdSuccessfully() {

        WebhookService webhookService = new WebhookService(webhookRepository, userRepository);
        Long userId = 1L;
        
        User user = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");
        user.setId(userId);
        
        Webhook webhook1 = new Webhook("Webhook Discord", "https://discord.com/webhook1", user);
        webhook1.setId(1L);
        
        Webhook webhook2 = new Webhook("Webhook Slack", "https://slack.com/webhook2", user);
        webhook2.setId(2L);
        
        List<Webhook> webhooks = Arrays.asList(webhook1, webhook2);

        when(userRepository.existsByIdAndDeletedFalse(userId)).thenReturn(true);
        when(webhookRepository.findByUserId(userId)).thenReturn(webhooks);

        List<WebhookResponse> responses = webhookService.getByUserId(userId);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getDescription()).isEqualTo("Webhook Discord");
        assertThat(responses.get(1).getDescription()).isEqualTo("Webhook Slack");
        assertThat(responses.get(0).getUserId()).isEqualTo(userId);
        assertThat(responses.get(1).getUserId()).isEqualTo(userId);
        verify(userRepository).existsByIdAndDeletedFalse(userId);
        verify(webhookRepository).findByUserId(userId);
    }

    @Test
    void webhookService_shouldThrowExceptionWhenGetByUserIdWithUserNotFound() {

        WebhookService webhookService = new WebhookService(webhookRepository, userRepository);
        Long userId = 999L;

        when(userRepository.existsByIdAndDeletedFalse(userId)).thenReturn(false);

        assertThatThrownBy(() -> webhookService.getByUserId(userId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("User not found");
        
        verify(userRepository).existsByIdAndDeletedFalse(userId);
        verify(webhookRepository, never()).findByUserId(anyLong());
    }

    @Test
    void webhookService_shouldReturnEmptyListWhenUserHasNoWebhooks() {

        WebhookService webhookService = new WebhookService(webhookRepository, userRepository);
        Long userId = 1L;

        when(userRepository.existsByIdAndDeletedFalse(userId)).thenReturn(true);
        when(webhookRepository.findByUserId(userId)).thenReturn(Arrays.asList());

        List<WebhookResponse> responses = webhookService.getByUserId(userId);

        assertThat(responses).isEmpty();
        verify(userRepository).existsByIdAndDeletedFalse(userId);
        verify(webhookRepository).findByUserId(userId);
    }

    @Test
    void webhookService_shouldDeleteWebhookSuccessfully() {

        WebhookService webhookService = new WebhookService(webhookRepository, userRepository);
        Long webhookId = 1L;
        
        User user = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");
        user.setId(1L);
        
        Webhook webhook = new Webhook("Webhook para Deletar", "https://test.com/webhook", user);
        webhook.setId(webhookId);

        when(webhookRepository.findById(webhookId)).thenReturn(Optional.of(webhook));

        webhookService.delete(webhookId);

        verify(webhookRepository).findById(webhookId);
        verify(webhookRepository).deleteById(webhookId);
    }

    @Test
    void webhookService_shouldThrowExceptionWhenDeleteWebhookNotFound() {

        WebhookService webhookService = new WebhookService(webhookRepository, userRepository);
        Long webhookId = 999L;

        when(webhookRepository.findById(webhookId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> webhookService.delete(webhookId))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Webhook not found");
        
        verify(webhookRepository).findById(webhookId);
        verify(webhookRepository, never()).deleteById(anyLong());
    }

    @Test
    void webhookService_shouldSendMessageByUserSuccessfully() {

        WebhookService webhookService = spy(new WebhookService(webhookRepository, userRepository));
        
        User user = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");
        user.setId(1L);
        
        Webhook webhook1 = new Webhook("Webhook 1", "https://discord.com/webhook1", user);
        webhook1.setId(1L);
        
        Webhook webhook2 = new Webhook("Webhook 2", "https://slack.com/webhook2", user);
        webhook2.setId(2L);
        
        List<Webhook> webhooks = Arrays.asList(webhook1, webhook2);
        String message = "Nova tarefa criada!";

        when(webhookRepository.findByUserId(user.getId())).thenReturn(webhooks);
        doNothing().when(webhookService).sendMessage(anyString(), anyString());

        webhookService.sendMessageByUser(user, message);

        verify(webhookRepository).findByUserId(user.getId());
        verify(webhookService).sendMessage("https://discord.com/webhook1", message);
        verify(webhookService).sendMessage("https://slack.com/webhook2", message);
    }

    @Test
    void webhookService_shouldNotSendMessageWhenUserHasNoWebhooks() {

        WebhookService webhookService = spy(new WebhookService(webhookRepository, userRepository));
        
        User user = new User("João", LocalDate.of(1990, 1, 1), "11999999999", "joao@test.com", "pass");
        user.setId(1L);
        
        String message = "Nova tarefa criada!";

        when(webhookRepository.findByUserId(user.getId())).thenReturn(Arrays.asList());

        webhookService.sendMessageByUser(user, message);

        verify(webhookRepository).findByUserId(user.getId());
        verify(webhookService, never()).sendMessage(anyString(), anyString());
    }
}