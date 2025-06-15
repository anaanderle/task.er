package com.uni.task.er.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.uni.task.er.exception.custom.UnauthorizedException; // Certifique-se que esta exceção existe
import org.springframework.stereotype.Service;
import com.google.api.client.auth.oauth2.Credential;


import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Date;

@Service
public class GoogleCalendarService {
    private static final String APPLICATION_NAME = "Task.er";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens"; // Diretório para armazenar tokens por usuário

    // Método para obter o serviço do Calendar para um usuário específico
    public Calendar getCalendarService(String userId) throws Exception {
        File credentialsFile = new File("credentials.json");
        if (!credentialsFile.exists()) {
            throw new RuntimeException("Arquivo credentials.json não encontrado!");
        }
        FileInputStream in = new FileInputStream(credentialsFile);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets,
                Collections.singletonList("https://www.googleapis.com/auth/calendar"))
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        Credential credential = flow.loadCredential(userId);
        if (credential == null) {
            throw new UnauthorizedException("Usuário não autenticado com o Google ou token não encontrado. Por favor, autorize o acesso ao Google Calendar.");
        }
        // Opcional: Adicionar lógica de refresh token se necessário, embora a biblioteca deva lidar com isso.
        // if (credential.getExpiresInSeconds() != null && credential.getExpiresInSeconds() <= 60) {
        //     credential.refreshToken();
        // }

        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    // Método para criar evento para um usuário específico
    public void criarEvento(String userId, String summary, String description, Date start, Date end) throws Exception {
        Calendar service = getCalendarService(userId);

        Event event = new Event()
                .setSummary(summary)
                .setDescription(description);

        EventDateTime startDateTime = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(start))
                .setTimeZone("America/Sao_Paulo");
        event.setStart(startDateTime);

        EventDateTime endDateTime = new EventDateTime()
                .setDateTime(new com.google.api.client.util.DateTime(end))
                .setTimeZone("America/Sao_Paulo");
        event.setEnd(endDateTime);

        service.events().insert("primary", event).execute();
    }

    // Gera a URL de autorização, incluindo o userId no parâmetro state
    public String getAuthorizationUrl(String userId) throws Exception {
        File credentialsFile = new File("credentials.json");
        if (!credentialsFile.exists()) {
            throw new RuntimeException("Arquivo credentials.json não encontrado!");
        }
        FileInputStream in = new FileInputStream(credentialsFile);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets,
                Collections.singletonList("https://www.googleapis.com/auth/calendar"))
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline") // Essencial para obter refresh_token
                .setApprovalPrompt("force") // Opcional: força o prompt de consentimento para garantir o refresh_token na primeira vez
                .build();

        String redirectUri = "http://localhost:8080/api/google/oauth-callback";
        return flow.newAuthorizationUrl().setRedirectUri(redirectUri).setState(userId).build();
    }

    // Troca o código de autorização por tokens e os armazena para o usuário (userIdFromState)
    public void exchangeCodeForTokens(String code, String userIdFromState) throws Exception {
        File credentialsFile = new File("credentials.json");
        if (!credentialsFile.exists()) {
            throw new RuntimeException("Arquivo credentials.json não encontrado!");
        }
        FileInputStream in = new FileInputStream(credentialsFile);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets,
                Collections.singletonList("https://www.googleapis.com/auth/calendar"))
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        String redirectUri = "http://localhost:8080/api/google/oauth-callback";
        GoogleTokenResponse tokenResponse = flow.newTokenRequest(code)
                .setRedirectUri(redirectUri)
                .execute();

        // Armazena o credential (incluindo refresh_token) para o userId específico
        flow.createAndStoreCredential(tokenResponse, userIdFromState);
    }
}