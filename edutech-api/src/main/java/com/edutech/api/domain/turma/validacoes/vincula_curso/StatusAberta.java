package com.edutech.api.domain.turma.validacoes.vincula_curso;

import com.edutech.api.domain.curso.Curso;
import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.turma.enums.StatusTurma;
import com.edutech.api.domain.turma.Turma;
import org.springframework.stereotype.Component;

@Component
public class StatusAberta implements ValidadorVinculoCurso {

    @Override
    public void validar(Turma turma, Curso curso) {

        if (turma.getStatus() != StatusTurma.ABERTA) {
            throw new ValidacaoException(
                    "Curso só pode ser vinculado a turmas com status ABERTA."
            );
        }
    }
}