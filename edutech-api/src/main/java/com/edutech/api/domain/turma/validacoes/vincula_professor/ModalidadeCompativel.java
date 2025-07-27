package com.edutech.api.domain.turma.validacoes.vincula_professor;

import com.edutech.api.domain.professor.Professor;
import com.edutech.api.domain.turma.Turma;
import com.edutech.api.domain.exception.ValidacaoException;
import org.springframework.stereotype.Component;

@Component
public class ModalidadeCompativel implements ValidadorVinculoProfessor {

    @Override
    public void validar(Turma turma, Professor professor) {

        if (!professor.getModalidade().equals(turma.getModalidade())) {
            throw new ValidacaoException(
                    "Modalidade do professor não é compativel com a turma"
            );
        }
    }
}
