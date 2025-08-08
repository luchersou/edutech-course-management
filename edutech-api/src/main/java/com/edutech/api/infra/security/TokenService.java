package com.edutech.api.infra.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class TokenService {

    private static final String ISSUER = "edutech_api";
    private static final long EXPIRATION_SECONDS = 7200L; // 2 horas

    private final JwtEncoder jwtEncoder;

    public String gerarToken(String subject) {
        Instant agora = Instant.now();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .issuedAt(agora)
                .expiresAt(agora.plusSeconds(EXPIRATION_SECONDS))
                .subject(subject)
                .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}