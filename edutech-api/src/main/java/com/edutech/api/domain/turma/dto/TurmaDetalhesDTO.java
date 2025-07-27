package com.edutech.api.domain.turma.dto;

import com.edutech.api.domain.enums.Modalidade;
import com.edutech.api.domain.turma.enums.StatusTurma;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;
import java.time.LocalTime;

public record TurmaDetalhesDTO(
        Long id,
        String codigo,

        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataInicio,

        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataFim,

        @JsonFormat(pattern = "HH:mm")
        LocalTime horarioInicio,

        @JsonFormat(pattern = "HH:mm")
        LocalTime horarioFim,

        Modalidade modalidade,
        Integer vagasTotais,
        Integer vagasDisponiveis,
        StatusTurma status
) {}
