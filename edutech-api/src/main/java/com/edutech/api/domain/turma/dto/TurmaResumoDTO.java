package com.edutech.api.domain.turma.dto;

import com.edutech.api.domain.turma.enums.StatusTurma;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record TurmaResumoDTO(
        Long id,
        String codigo,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataInicio,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataFim,
        StatusTurma status
) {}
