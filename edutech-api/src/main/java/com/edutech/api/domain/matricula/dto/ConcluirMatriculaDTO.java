package com.edutech.api.domain.matricula.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ConcluirMatriculaDTO(
        @NotNull(message = "Nota é obrigatória")
        @DecimalMin(value = "0.0", message = "Nota deve ser maior ou igual a 0")
        @DecimalMax(value = "10.0", message = "Nota deve ser menor ou igual a 10")
        BigDecimal notaFinal
) {}
