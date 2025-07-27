package com.edutech.api.domain.turma.validacoes.vincula_professor;

import com.edutech.api.domain.professor.Professor;
import com.edutech.api.domain.turma.Turma;

public interface ValidadorVinculoProfessor {
    void validar(Turma turma, Professor professor);
}
