package com.edutech.api.domain.turma.validacoes.vincula_curso;

import com.edutech.api.domain.curso.Curso;
import com.edutech.api.domain.curso.enums.StatusCurso;
import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.turma.Turma;
import org.springframework.stereotype.Component;

@Component
public class StatusTurma implements ValidadorVinculoCurso {

    @Override
    public void validar(Turma turma, Curso curso) {

        if (curso.getStatus() != StatusCurso.ATIVO) {
            throw new ValidacaoException(
                    "Só é possível vincular cursos com status ATIVO."
            );
        }
    }
}