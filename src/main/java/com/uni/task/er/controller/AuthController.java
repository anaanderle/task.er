package com.uni.task.er.controller;

import com.uni.task.er.dto.request.AuthLoginRequest;
import com.uni.task.er.dto.response.AuthLoginResponse; // Importar o DTO de resposta
import com.uni.task.er.exception.TaskerException;
import com.uni.task.er.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity; // Para retornar ResponseEntity
import org.springframework.web.bind.annotation.*;

@Tag(name = "Autenticação")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(description = "Login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna o JWT token e o ID do usuário",
                         content = @Content(schema = @Schema(implementation = AuthLoginResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Não autorizado",
                    content = @Content(schema = @Schema(implementation = TaskerException.class))),
    })
    @PostMapping
    public ResponseEntity<AuthLoginResponse> login(@RequestBody AuthLoginRequest request) {
        AuthLoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(description = "Logout")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Realize o logout com sucesso"),
    })
    @DeleteMapping
    public void logout(@RequestHeader("Authorization") String bearerToken) {
        authService.logout(bearerToken);
    }
}
