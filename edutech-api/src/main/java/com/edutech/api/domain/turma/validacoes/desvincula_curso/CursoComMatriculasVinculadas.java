package com.edutech.api.domain.turma.validacoes.desvincula_curso;

import com.edutech.api.domain.curso.Curso;
import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.turma.Turma;
import org.springframework.stereotype.Component;

@Component
public class CursoComMatriculasVinculadas implements ValidadorDesvinculoCurso {

    @Override
    public void validar(Turma turma, Curso curso) {
        if (!turma.getMatriculas().isEmpty()) {
            throw new ValidacaoException(
                    "Não é possivel desvincular o curso de uma turma com alunos matriculados."
            );
        }
    }
}
