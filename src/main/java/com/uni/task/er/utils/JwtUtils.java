package com.uni.task.er.utils;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtils {
    private static final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION = 1000 * 60 * 60; // 1 hora

    public static String generateToken(String username, Long userId) { // Adicionado userId
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("userId", userId); // Adiciona userId ao payload do token

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key)
                .compact();
    }

    public static Claims validateTokenAndGetClaims(String token) { // Mudado para retornar Claims
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Método antigo mantido para compatibilidade se ainda usado em algum lugar, mas idealmente seria removido/refatorado
    public static String validateTokenAndGetUsername(String token) {
        return validateTokenAndGetClaims(token).getSubject();
    }

    public static Long getUserIdFromToken(String token) {
        Claims claims = validateTokenAndGetClaims(token);
        return claims.get("userId", Long.class);
    }
}