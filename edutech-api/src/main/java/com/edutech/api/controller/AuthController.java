package com.edutech.api.controller;

import com.edutech.api.domain.usuario.Usuario;
import com.edutech.api.domain.usuario.dto.LoginDTO;
import com.edutech.api.infra.dto.TokenDTO;
import com.edutech.api.infra.security.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager manager;
    private final TokenService tokenService;

    @PostMapping
    public ResponseEntity login(@RequestBody @Valid LoginDTO dto) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dto.login(), dto.senha());
        var authentication = manager.authenticate(authenticationToken);
        var usuario = (Usuario) authentication.getPrincipal();
        var tokenJWT = tokenService.gerarToken(usuario.getLogin());

        return ResponseEntity.ok(new TokenDTO(tokenJWT));
    }
}
