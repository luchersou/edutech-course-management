package com.edutech.api.infra.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@Service
public class TokenService {

    @Value("${api.security.token.secret}")
    private String segredo;

    @Value("${api.security.token.expiration:7200}")
    private Long expiracaoEmSegundos;

    private static final String ISSUER = "edutech_api";

    public String gerarToken(String subject) {
        return Jwts.builder()
                .issuer(ISSUER)
                .subject(subject)
                .issuedAt(Date.from(Instant.now()))
                .expiration(obterDataExpiracao())
                .signWith(obterChaveAssinatura(), Jwts.SIG.HS256)
                .compact();
    }

    public String obterSujeito(String token) {
        return obterClaims(token).getSubject();
    }

    public boolean tokenValido(String token) {
        return obterClaims(token) != null;
    }

    private Claims obterClaims(String token) {
        return Jwts.parser()
                .verifyWith(obterChaveAssinatura())
                .requireIssuer(ISSUER)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey obterChaveAssinatura() {
        return Keys.hmacShaKeyFor(segredo.getBytes(StandardCharsets.UTF_8));
    }

    private Date obterDataExpiracao() {
        return Date.from(
                LocalDateTime.now()
                        .plusSeconds(expiracaoEmSegundos)
                        .toInstant(ZoneOffset.of("-03:00"))
        );
    }
}