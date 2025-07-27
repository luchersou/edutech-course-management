package com.edutech.api.domain.aluno.dto;

import com.edutech.api.domain.aluno.enums.StatusAluno;

public record AlunoResumoDTO(
        Long id,
        String nome,
        String email,
        StatusAluno status
) {}