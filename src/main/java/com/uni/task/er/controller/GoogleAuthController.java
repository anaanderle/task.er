package com.uni.task.er.controller;

import com.uni.task.er.model.User; // Importa a classe User
import com.uni.task.er.service.GoogleCalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/google")
public class GoogleAuthController {
    private final GoogleCalendarService googleCalendarService;

    @Autowired
    public GoogleAuthController(GoogleCalendarService googleCalendarService) {
        this.googleCalendarService = googleCalendarService;
    }

    @GetMapping("/auth-url")
    public String getGoogleAuthUrl(@RequestAttribute("user") User user) throws Exception {
        // Passa o ID do usuário para ser usado no fluxo OAuth e armazenar o token
        return googleCalendarService.getAuthorizationUrl(user.getId().toString());
    }

    @GetMapping("/oauth-callback")
    public String handleGoogleCallback(@RequestParam("code") String code, @RequestParam("state") String userIdFromState) throws Exception {
        googleCalendarService.exchangeCodeForTokens(code, userIdFromState);
        // Idealmente, redirecionar para uma página de sucesso no frontend
        return "Autenticação com Google realizada com sucesso! Você já pode fechar esta janela.";
    }
}
