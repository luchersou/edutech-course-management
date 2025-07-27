package com.edutech.api.domain.turma.validacoes.desvincula_curso;

import com.edutech.api.domain.curso.Curso;
import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.turma.enums.StatusTurma;
import com.edutech.api.domain.turma.Turma;
import org.springframework.stereotype.Component;

@Component
public class ValidacaoStatusTurma implements ValidadorDesvinculoCurso {

    @Override
    public void validar(Turma turma, Curso curso) {
        if (turma.getStatus() != StatusTurma.ABERTA) {
            throw new ValidacaoException(
                    "Só é possivel desvincular curso de turmas com status ABERTA."
            );
        }
    }
}

