package com.edutech.api.domain.turma.validacoes.atualiza_turma;

import com.edutech.api.domain.turma.Turma;
import com.edutech.api.domain.turma.dto.TurmaUpdateDTO;

public interface ValidadorAtualizaTurma {
    void validar(Turma turmaId, TurmaUpdateDTO dto);
}
