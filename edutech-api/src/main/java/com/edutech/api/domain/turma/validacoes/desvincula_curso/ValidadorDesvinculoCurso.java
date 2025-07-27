package com.edutech.api.domain.turma.validacoes.desvincula_curso;

import com.edutech.api.domain.curso.Curso;
import com.edutech.api.domain.turma.Turma;

public interface ValidadorDesvinculoCurso {
    void validar(Turma turma, Curso curso);
}

