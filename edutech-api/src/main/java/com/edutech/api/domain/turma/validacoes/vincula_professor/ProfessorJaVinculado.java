package com.edutech.api.domain.turma.validacoes.vincula_professor;

import com.edutech.api.domain.professor.Professor;
import com.edutech.api.domain.turma.Turma;
import com.edutech.api.domain.exception.ValidacaoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProfessorJaVinculado implements ValidadorVinculoProfessor {

    @Override
    public void validar(Turma turma, Professor professor) {
        if (turma.getProfessor() != null) {
            throw new ValidacaoException(
                    "Esta turma ja possui um professor vinculado."
            );
        }
    }
}