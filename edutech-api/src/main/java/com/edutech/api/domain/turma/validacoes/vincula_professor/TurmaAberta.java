package com.edutech.api.domain.turma.validacoes.vincula_professor;

import com.edutech.api.domain.professor.Professor;
import com.edutech.api.domain.turma.enums.StatusTurma;
import com.edutech.api.domain.turma.Turma;
import com.edutech.api.domain.exception.ValidacaoException;
import org.springframework.stereotype.Component;

@Component
public class TurmaAberta implements ValidadorVinculoProfessor {

    @Override
    public void validar(Turma turma, Professor professor) {

        if (turma.getStatus() != StatusTurma.ABERTA) {
            throw new ValidacaoException(
                    "Só é possível vincular professor a turmas abertas"
            );
        }
    }
}