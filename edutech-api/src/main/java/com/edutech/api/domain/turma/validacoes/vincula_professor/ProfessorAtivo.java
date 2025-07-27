package com.edutech.api.domain.turma.validacoes.vincula_professor;

import com.edutech.api.domain.professor.enums.StatusProfessor;
import com.edutech.api.domain.professor.Professor;
import com.edutech.api.domain.turma.Turma;
import com.edutech.api.domain.exception.ValidacaoException;
import org.springframework.stereotype.Component;

@Component
public class ProfessorAtivo implements ValidadorVinculoProfessor {

    @Override
    public void validar(Turma turma, Professor professor) {
        if (professor.getStatus() != StatusProfessor.ATIVO) {
            throw new ValidacaoException(
                    "Professor deve estar ativo para ser vinculado à turma"
            );
        }
    }
}
