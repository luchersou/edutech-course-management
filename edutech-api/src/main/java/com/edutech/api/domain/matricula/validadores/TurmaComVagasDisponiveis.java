package com.edutech.api.domain.matricula.validadores;

import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.matricula.dto.MatriculaCreateDTO;
import com.edutech.api.domain.matricula.repository.MatriculaRepository;
import com.edutech.api.domain.turma.repository.TurmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TurmaComVagasDisponiveis implements ValidadorCadastroMatricula {

    private final TurmaRepository turmaRepository;
    private final MatriculaRepository matriculaRepository;

    @Override
    public void validar(MatriculaCreateDTO dto) {
        if (dto.turmaId() == null) {
            return;
        }

        var turma = turmaRepository.findById(dto.turmaId())
                .orElseThrow(() -> new ValidacaoException(
                        "Turma informada não existe"
                ));

        long matriculados = matriculaRepository.countByTurmaId(dto.turmaId());
        int vagas = turma.getVagasTotais();

        if (matriculados >= vagas) {
            throw new ValidacaoException(
                    "A turma esta com todas as vagas preenchidas"
            );
        }
    }
}
