package com.edutech.api.domain.matricula.dto;

import com.edutech.api.domain.matricula.enums.MotivoCancelamento;
import com.edutech.api.domain.matricula.enums.StatusMatricula;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MatriculaDetalhesDTO(
        Long id,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataMatricula,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataConclusao,
        BigDecimal notaFinal,
        StatusMatricula status,
        MotivoCancelamento motivoCancelamento,
        Long alunoId,
        String nomeAluno,
        Long turmaId,
        String codigoTurma
) {}
