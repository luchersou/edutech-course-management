package com.edutech.api.domain.turma.validacoes.desvincula_professor;

import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.professor.Professor;
import com.edutech.api.domain.turma.enums.StatusTurma;
import com.edutech.api.domain.turma.Turma;
import org.springframework.stereotype.Component;

@Component
public class StatusTurmaNaoPermite implements ValidadorDesvinculoProfessor {

    @Override
    public void validar(Turma turma, Professor professor) {

        if (turma.getStatus() == StatusTurma.EM_ANDAMENTO || turma.getStatus() == StatusTurma.CONCLUIDA) {
            throw new ValidacaoException(
                    "Não é possivel desvincular professor de turma em andamento ou finalizada."
            );
        }
    }
}