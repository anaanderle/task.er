package com.uni.task.er.config;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.DataStoreCredentialRefreshListener;
import com.google.api.client.auth.oauth2.StoredCredential;
import com.google.api.client.util.store.AbstractDataStore;
import com.google.api.client.util.store.DataStore;
import com.google.api.client.util.store.DataStoreFactory;
import com.uni.task.er.model.GoogleToken;
import com.uni.task.er.repository.GoogleTokenRepository;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DatabaseDataStoreFactory implements DataStoreFactory {
    
    private final GoogleTokenRepository googleTokenRepository;
    
    public DatabaseDataStoreFactory(GoogleTokenRepository googleTokenRepository) {
        this.googleTokenRepository = googleTokenRepository;
    }
    
    @Override
    public <V extends Serializable> DataStore<V> getDataStore(String id) throws IOException {
        return new DatabaseDataStore<>(this, id, googleTokenRepository);
    }
    
    private static class DatabaseDataStore<V extends Serializable> extends AbstractDataStore<V> {
        
        private final GoogleTokenRepository googleTokenRepository;
        
        protected DatabaseDataStore(DataStoreFactory dataStoreFactory, String id, GoogleTokenRepository googleTokenRepository) {
            super(dataStoreFactory, id);
            this.googleTokenRepository = googleTokenRepository;
        }
        
        @Override
        public DataStore<V> set(String key, V value) throws IOException {
            if (value instanceof StoredCredential) {
                StoredCredential credential = (StoredCredential) value;
                
                GoogleToken existingToken = googleTokenRepository.findByUserId(key).orElse(null);
                if (existingToken != null) {
                    existingToken.setAccessToken(credential.getAccessToken());
                    existingToken.setRefreshToken(credential.getRefreshToken());
                    existingToken.setExpiresIn(credential.getExpirationTimeMilliseconds());
                    googleTokenRepository.save(existingToken);
                } else {
                    GoogleToken newToken = new GoogleToken(
                        key,
                        credential.getAccessToken(),
                        credential.getRefreshToken(),
                        credential.getExpirationTimeMilliseconds(),
                        "Bearer"
                    );
                    googleTokenRepository.save(newToken);
                }
            }
            return this;
        }
        
        @Override
        public V get(String key) throws IOException {
            GoogleToken token = googleTokenRepository.findByUserId(key).orElse(null);
            if (token != null) {
                StoredCredential credential = new StoredCredential();
                credential.setAccessToken(token.getAccessToken());
                credential.setRefreshToken(token.getRefreshToken());
                credential.setExpirationTimeMilliseconds(token.getExpiresIn());
                return (V) credential;
            }
            return null;
        }
        
        @Override
        public DataStore<V> clear() throws IOException {
            googleTokenRepository.deleteAll();
            return this;
        }
        
        @Override
        public DataStore<V> delete(String key) throws IOException {
            googleTokenRepository.deleteByUserId(key);
            return this;
        }
        
        @Override
        public Set<String> keySet() throws IOException {
            return googleTokenRepository.findAll().stream()
                .map(GoogleToken::getUserId)
                .collect(java.util.stream.Collectors.toSet());
        }
        
        @Override
        public Collection<V> values() throws IOException {
            return googleTokenRepository.findAll().stream()
                .map(token -> {
                    StoredCredential credential = new StoredCredential();
                    credential.setAccessToken(token.getAccessToken());
                    credential.setRefreshToken(token.getRefreshToken());
                    credential.setExpirationTimeMilliseconds(token.getExpiresIn());
                    return (V) credential;
                })
                .toList();
        }
        
        @Override
        public boolean containsKey(String key) throws IOException {
            return googleTokenRepository.findByUserId(key).isPresent();
        }
        
        @Override
        public boolean containsValue(V value) throws IOException {
            return values().contains(value);
        }
        
        @Override
        public boolean isEmpty() throws IOException {
            return googleTokenRepository.count() == 0;
        }
        
        @Override
        public int size() throws IOException {
            return (int) googleTokenRepository.count();
        }
    }
}
