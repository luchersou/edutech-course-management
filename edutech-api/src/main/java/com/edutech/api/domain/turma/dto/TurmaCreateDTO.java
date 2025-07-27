package com.edutech.api.domain.turma.dto;

import com.edutech.api.domain.enums.Modalidade;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

public record TurmaCreateDTO(
        @NotBlank(message = "Código é obrigatório")
        @Size(min = 3, max = 20)
        String codigo,

        @NotNull(message = "Data de inicio é obrigatória")
        @Future(message = "Data de inicio deve ser futura")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataInicio,

        @NotNull(message = "Data de fim é obrigatória")
        @Future(message = "Data de fim deve ser futura")
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataFim,

        @NotNull(message = "Horario de inicio da turma é obrigatória")
        @JsonFormat(pattern = "HH:mm")
        LocalTime horarioInicio,

        @NotNull(message = "Horario de fim da turma é obrigatória")
        @JsonFormat(pattern = "HH:mm")
        LocalTime horarioFim,

        @NotNull(message = "Numero de vagas é obrigatório")
        @Positive(message = "Numero de vagas deve ser positivo")
        Integer vagasTotais,

        @NotNull(message = "Modalidade é obrigatória")
        Modalidade modalidade
) {}
