package com.edutech.api.domain.turma.validacoes.desvincula_curso;

import com.edutech.api.domain.curso.Curso;
import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.turma.Turma;
import org.springframework.stereotype.Component;

@Component("cursoNaoVinculadoIniciarTurma")
public class CursoNaoVinculado implements ValidadorDesvinculoCurso{

    @Override
    public void validar(Turma turma, Curso curso) {

        if (turma.getCurso() == null || !turma.getCurso().getId().equals(curso.getId())) {
            throw new ValidacaoException(
                    "Esse curso não está vinculado a turma"
            );
        }
    }
}
