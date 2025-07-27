package com.edutech.api.domain.professor.validacoes;

import com.edutech.api.domain.professor.dto.ProfessorCreateDTO;

public interface ValidadorCadastroProfessor {
    void validar(ProfessorCreateDTO dto);
}