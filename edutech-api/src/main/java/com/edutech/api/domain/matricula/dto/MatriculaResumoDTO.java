package com.edutech.api.domain.matricula.dto;

import com.edutech.api.domain.matricula.enums.StatusMatricula;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record MatriculaResumoDTO(
        Long id,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataMatricula,
        Long alunoId,
        String nomeAluno,
        Long cursoId,
        String nomeCurso,
        Long turmaId,
        String codigoTurma,
        StatusMatricula status
) {}
