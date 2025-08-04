package com.edutech.api.infra.security;

import com.edutech.api.infra.dto.DadosErroResposta;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class HandlerAcessoNegadoPersonalizado implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, org.springframework.security.access.AccessDeniedException accessDeniedException) throws IOException, ServletException {
        String usuario = getUsuarioLogado();
        log.warn("Acesso negado - Usuario: {} - Endpoint: {}", usuario, request.getRequestURI());

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        DadosErroResposta dadosErro = new DadosErroResposta(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                "Acesso negado. Você não tem permissão para acessar este recurso."
        );

        response.getWriter().write(objectMapper.writeValueAsString(dadosErro));
    }

    private String getUsuarioLogado() {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            return auth != null ? auth.getName() : "Anônimo";
        } catch (Exception e) {
            return "Desconhecido";
        }
    }
}