package com.edutech.api.domain.turma.validacoes.cadastra_turma;

import com.edutech.api.domain.turma.dto.TurmaCreateDTO;

public interface ValidadorCadastroTurma {
    void validar(TurmaCreateDTO dto);
}
