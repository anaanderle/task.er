package com.uni.task.er.controller;

import com.uni.task.er.dto.request.WebhookCreateRequest;
import com.uni.task.er.dto.response.WebhookResponse;
import com.uni.task.er.exception.TaskerException;
import com.uni.task.er.service.WebhookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Webhooks")
@RestController
@RequestMapping("/webhooks")
public class WebhookController {

    private final WebhookService webhookService;

    @Autowired
    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @Operation(description = "Busca o webhook pelo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna o webhook"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Webhook não encontrado",
                    content = @Content(schema = @Schema(implementation = TaskerException.class))),
    })
    @GetMapping("/{id}")
    public WebhookResponse getById(@PathVariable long id) {
        return webhookService.getById(id);
    }

    @Operation(description = "Busca os webhooks de um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna os webhooks do usuário"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = TaskerException.class))),
    })
    @GetMapping("/user/{id}")
    public List<WebhookResponse> getByUserId(@PathVariable long id) {
        return webhookService.getByUserId(id);
    }

    @Operation(description = "Cria um webhook")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna o webhook criado"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = TaskerException.class))),
    })
    @PostMapping
    public WebhookResponse create(@RequestBody WebhookCreateRequest request) {
        return webhookService.create(request);
    }

    @Operation(description = "Exclui um webhook")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Webhook excluído com sucesso"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Webhook não encontrado",
                    content = @Content(schema = @Schema(implementation = TaskerException.class))),
    })
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        webhookService.delete(id);
    }
}
