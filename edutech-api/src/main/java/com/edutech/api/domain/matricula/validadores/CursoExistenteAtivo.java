package com.edutech.api.domain.matricula.validadores;

import com.edutech.api.domain.curso.enums.StatusCurso;
import com.edutech.api.domain.curso.repository.CursoRepository;
import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.matricula.dto.MatriculaCreateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CursoExistenteAtivo implements ValidadorCadastroMatricula{

    private final CursoRepository cursoRepository;

    @Override
    public void validar(MatriculaCreateDTO dto) {
        var curso = cursoRepository.findById(dto.cursoId())
                .orElseThrow(() -> new ValidacaoException(
                        "Curso com ID " + dto.cursoId() + " não encontrado"
                ));

        if (curso.getStatus() != StatusCurso.ATIVO) {
            throw new ValidacaoException(
                    "Curso inativo para novas matriculas"
            );
        }
    }
}

