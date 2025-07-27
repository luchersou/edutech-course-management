package com.edutech.api.domain.aluno.validacoes;

import com.edutech.api.domain.aluno.dto.AlunoCreateDTO;

public interface ValidadorCadastroAluno {
    void validar(AlunoCreateDTO dto);
}
