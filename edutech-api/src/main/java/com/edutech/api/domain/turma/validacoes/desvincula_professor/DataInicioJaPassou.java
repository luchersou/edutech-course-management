package com.edutech.api.domain.turma.validacoes.desvincula_professor;

import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.professor.Professor;
import com.edutech.api.domain.turma.Turma;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInicioJaPassou implements ValidadorDesvinculoProfessor {

    @Override
    public void validar(Turma turma, Professor professor) {

        if (turma.getDataInicio() != null && turma.getDataInicio().isBefore(LocalDate.now())) {
            throw new ValidacaoException(
                    "Não é possivel desvincular professor após o inicio da turma."
            );
        }
    }
}