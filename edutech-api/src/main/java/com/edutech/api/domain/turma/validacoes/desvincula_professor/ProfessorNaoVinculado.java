package com.edutech.api.domain.turma.validacoes.desvincula_professor;

import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.professor.Professor;
import com.edutech.api.domain.turma.Turma;
import org.springframework.stereotype.Component;

@Component("ProfessorNaoVinculadoDesvincularProfessor")
public class ProfessorNaoVinculado implements ValidadorDesvinculoProfessor {

    @Override
    public void validar(Turma turma, Professor professor) {

        if (turma.getProfessor() == null || !turma.getProfessor().getId().equals(professor.getId())) {
            throw new ValidacaoException(
                    "Esse professor não está vinculado a turma"
            );
        }
    }
}
