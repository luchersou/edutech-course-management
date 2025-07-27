package com.edutech.api.domain.turma.dto;

import com.edutech.api.domain.enums.Modalidade;
import com.edutech.api.domain.turma.enums.StatusTurma;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record TurmaUpdateDTO(
        @Size(min = 3, max = 20)
        String codigo,

        @Future(message = "Data de fim deve ser futura")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataInicio,

        @Future(message = "Data de fim deve ser futura")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataFim,

        @JsonFormat(pattern = "HH:mm")
        LocalTime horarioInicio,

        @JsonFormat(pattern = "HH:mm")
        LocalTime horarioFim,

        @Positive(message = "Numero de vagas deve ser positivo")
        Integer vagasTotais,

        Modalidade modalidade
) {}
