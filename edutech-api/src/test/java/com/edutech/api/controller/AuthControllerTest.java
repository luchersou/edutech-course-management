package com.edutech.api.controller;

import com.edutech.api.domain.usuario.Usuario;
import com.edutech.api.domain.usuario.dto.LoginDTO;
import com.edutech.api.infra.dto.TokenDTO;
import com.edutech.api.infra.security.TokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("Testes unitários do auth controller")
@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    @InjectMocks
    private AuthController controller;

    @Mock
    private AuthenticationManager manager;

    @Mock
    private TokenService tokenService;

    @Test
    @DisplayName("Deve autenticar um usuário com credenciais válidas e gerar um token JWT com sucesso")
    void deveAutenticarEGerarTokenComSucesso() {
        String login = "joao";
        String senha = "1234";
        String tokenGerado = "mock-jwt";

        LoginDTO dto = new LoginDTO(login, senha);

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(login, senha);

        Usuario usuarioMock = Usuario.builder()
                .login(login)
                .build();

        Authentication authenticationMock = mock(Authentication.class);
        when(authenticationMock.getPrincipal()).thenReturn(usuarioMock);

        when(manager.authenticate(any())).thenReturn(authenticationMock);
        when(tokenService.gerarToken(login)).thenReturn(tokenGerado);

        ResponseEntity response = controller.login(dto);

        assertAll(
                () -> {
                    assertNotNull(response, "A resposta não deveria ser nula");
                    assertEquals(HttpStatus.OK, response.getStatusCode());
                },
                () -> {
                    assertNotNull(response.getBody(), "O corpo da resposta não deveria ser nulo");
                    TokenDTO body = assertInstanceOf(TokenDTO.class, response.getBody());
                    assertEquals(tokenGerado, body.token());
                }
        );

        verify(manager).authenticate(authToken);
        verify(tokenService).gerarToken(login);
    }

}
