package com.edutech.api.domain.turma.validacoes.vincula_curso;

import com.edutech.api.domain.curso.Curso;
import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.professor.Professor;
import com.edutech.api.domain.turma.Turma;
import com.edutech.api.domain.turma.repository.TurmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CursoJaVinculado implements ValidadorVinculoCurso {

    @Override
    public void validar(Turma turma, Curso curso) {
        if (turma.getCurso() != null) {
            throw new ValidacaoException(
                    "Essa turma ja possui um curso vinculado."
            );
        }
    }
}
