package com.edutech.api.domain.turma.validacoes.inicia_turma;

import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.turma.Turma;
import org.springframework.stereotype.Component;

@Component
public class AlunosNaoMatriculados implements ValidadorIniciarTurma{

    @Override
    public void validar(Turma turma) {
        if (turma.getMatriculas() == null || turma.getMatriculas().isEmpty()) {
            throw new ValidacaoException(
                    "Não é possível iniciar uma turma sem alunos matriculados."
            );
        }
    }
}
