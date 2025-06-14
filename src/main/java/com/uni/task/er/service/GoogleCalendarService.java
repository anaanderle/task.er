package com.uni.task.er.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Date;

@Service
public class GoogleCalendarService {
    private static final String APPLICATION_NAME = "Task.er";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";

    public Calendar getCalendarService() throws Exception {
        File credentialsFile = new File("credentials.json"); // Busca no diretório de trabalho do container
        if (!credentialsFile.exists()) {
            throw new RuntimeException("Arquivo credentials.json não encontrado no diretório de trabalho!");
        }
        File tokensDir = new File(TOKENS_DIRECTORY_PATH);
        if (!tokensDir.exists()) {
            tokensDir.mkdirs();
        }
        FileInputStream in = new FileInputStream(credentialsFile);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets,
                Collections.singletonList("https://www.googleapis.com/auth/calendar"))
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();

        com.google.api.client.auth.oauth2.Credential credential =
                new com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp(
                        flow,
                        new com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver.Builder().setPort(8888).build()
                ).authorize("user");

        return new Calendar.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public void criarEvento(String summary, String description, Date start, Date end) throws Exception {
        Calendar service = getCalendarService();

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
}