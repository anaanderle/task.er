package com.uni.task.er.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
public class JwtBlocklistService {

    private Cache<String, Boolean> blocklistCache = Caffeine.newBuilder().expireAfterWrite(1, TimeUnit.HOURS).build();

    public void blocklist(String token) {
        blocklistCache.put(token, true);
    }

    public boolean isBlocklisted(String token) {
        return Objects.equals(blocklistCache.getIfPresent(token), Boolean.TRUE);
    }
}
