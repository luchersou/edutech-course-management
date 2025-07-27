package com.edutech.api.domain.matricula.validadores;

import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.matricula.dto.MatriculaCreateDTO;
import com.edutech.api.domain.turma.repository.TurmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TurmaPertenceAoCurso implements ValidadorCadastroMatricula{

    private final TurmaRepository turmaRepository;

    @Override
    public void validar(MatriculaCreateDTO dto) {

        if (dto.turmaId() == null) return;

        var turma = turmaRepository.findById(dto.turmaId())
                .orElseThrow(() -> new ValidacaoException(
                        "Turma com ID " + dto.turmaId() + " não encontrada"));

        if (!turma.getCurso().getId().equals(dto.cursoId())) {
            throw new ValidacaoException(
                    "A turma " + turma.getCodigo() + " não pertence ao curso " + dto.cursoId()
            );
        }
    }
}

