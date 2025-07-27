package com.edutech.api.domain.turma.validacoes.desvincula_curso;

import com.edutech.api.domain.curso.Curso;
import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.turma.Turma;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataDeInicioJaPassou implements ValidadorDesvinculoCurso {

    @Override
    public void validar(Turma turma, Curso curso) {
        if (turma.getDataInicio() != null && turma.getDataInicio().isBefore(LocalDate.now())) {
            throw new ValidacaoException(
                    "Não é possivel desvincular o curso após o inicio da turma."
            );
        }
    }
}
