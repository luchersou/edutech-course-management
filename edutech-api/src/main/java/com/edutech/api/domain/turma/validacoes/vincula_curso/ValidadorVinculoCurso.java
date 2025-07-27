package com.edutech.api.domain.turma.validacoes.vincula_curso;

import com.edutech.api.domain.curso.Curso;
import com.edutech.api.domain.turma.Turma;

public interface ValidadorVinculoCurso {
    void validar(Turma turma, Curso curso);
}
