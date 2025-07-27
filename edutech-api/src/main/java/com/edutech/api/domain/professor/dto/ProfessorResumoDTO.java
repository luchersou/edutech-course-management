package com.edutech.api.domain.professor.dto;

import com.edutech.api.domain.enums.Modalidade;
import com.edutech.api.domain.professor.enums.StatusProfessor;

public record ProfessorResumoDTO(
        Long id,
        String nome,
        String email,
        Modalidade modalidade,
        StatusProfessor status
) {}
