package com.edutech.api.domain.matricula.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record MatriculaCreateDTO(
        @NotNull
        Long alunoId,

        @NotNull
        Long turmaId,

        @NotNull
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataMatricula
) {}
