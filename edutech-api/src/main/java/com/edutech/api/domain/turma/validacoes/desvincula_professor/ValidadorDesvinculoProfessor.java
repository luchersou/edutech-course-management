package com.edutech.api.domain.turma.validacoes.desvincula_professor;

import com.edutech.api.domain.professor.Professor;
import com.edutech.api.domain.turma.Turma;

public interface ValidadorDesvinculoProfessor {
    void validar(Turma turma, Professor professor);
}

