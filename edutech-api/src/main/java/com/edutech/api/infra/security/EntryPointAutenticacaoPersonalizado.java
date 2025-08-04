package com.edutech.api.infra.security;

import com.edutech.api.infra.dto.DadosErroResposta;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class EntryPointAutenticacaoPersonalizado implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.warn("Falha de autenticação - Requisição para: {} - Erro: {}", request.getRequestURI(), authException.getMessage());

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        DadosErroResposta dadosErro = new DadosErroResposta(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Token de autenticação ausente ou inválido. Faça login para continuar."
        );

        response.getWriter().write(objectMapper.writeValueAsString(dadosErro));
    }
}
