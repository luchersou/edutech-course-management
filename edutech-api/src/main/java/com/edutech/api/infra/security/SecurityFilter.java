package com.edutech.api.infra.security;

import com.edutech.api.infra.dto.DadosErroResposta;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = extrairToken(request);

        if (token != null) {
            try {
                if (tokenService.tokenValido(token)) {
                    String subject = tokenService.obterSujeito(token);
                    autenticarUsuario(subject);
                }
            } catch (ExpiredJwtException ex) {
                log.warn("Token expirado: {}", ex.getMessage());
                enviarErroTokenExpirado(response);
                return;
            } catch (Exception e) {
                log.debug("Falha ao validar token: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extrairToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        return null;
    }

    private void autenticarUsuario(String subject) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(subject);

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void enviarErroTokenExpirado(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        DadosErroResposta erro = new DadosErroResposta(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Token de autenticação expirado. Faça login novamente."
        );

        try {
            String mensagemErro = objectMapper.writeValueAsString(erro);
            response.getWriter().write(mensagemErro);
            response.getWriter().flush();
        } catch (IOException e) {
            log.error("Erro ao escrever resposta de token expirado: {}", e.getMessage(), e);
        }
    }
}