package com.edutech.api.infra.config;

import com.edutech.api.domain.usuario.Usuario;
import com.edutech.api.domain.usuario.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (usuarioRepository.count() == 0) {
            var user = Usuario.builder()
                    .login("user")
                    .senha(passwordEncoder.encode("user123"))
                    .build();
            usuarioRepository.save(user);

            System.out.println("Usuário criado. Login: user, Senha: user123");
        }
    }
}
