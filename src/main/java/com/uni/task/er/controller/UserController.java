package com.uni.task.er.controller;

import com.uni.task.er.dto.request.UserCreateRequest;
import com.uni.task.er.dto.request.UserUpdateRequest;
import com.uni.task.er.dto.response.UserResponse;
import com.uni.task.er.exception.TaskerException;
import com.uni.task.er.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Usuários")
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(description = "Busca o usuário pelo id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna o usuário"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = TaskerException.class))),
    })
    @GetMapping("/{id}")
    public UserResponse getUserById(@PathVariable long id) {
        return userService.getById(id);
    }

    @Operation(description = "Cria um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna o usuário criado"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = TaskerException.class))),
    })
    @PostMapping
    public UserResponse create(@RequestBody UserCreateRequest request) {
        return userService.create(request);
    }

    @Operation(description = "Atualiza um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Retorna o usuário atualizado"),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados inválidos",
                    content = @Content(schema = @Schema(implementation = TaskerException.class))),
    })
    @PutMapping("/{id}")
    public UserResponse update(@PathVariable long id, @RequestBody UserUpdateRequest request) {
        return userService.update(id, request);
    }

    @Operation(description = "Exclui um usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso"),
            @ApiResponse(
                    responseCode = "404",
                    description = "Usuário não encontrado",
                    content = @Content(schema = @Schema(implementation = TaskerException.class))),
    })
    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        userService.delete(id);
    }
}
