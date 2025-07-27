package com.edutech.api.domain.turma.dto;

import com.edutech.api.domain.turma.enums.StatusTurma;

import java.util.List;

public record TurmaComMatriculasDTO(
        Long id,
        String codigo,
        StatusTurma status,
        Long professorId,
        Long cursoId,
        List<Long> matriculasIds
) {}
