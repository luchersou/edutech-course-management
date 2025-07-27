package com.edutech.api.domain.curso.dto;

import com.edutech.api.domain.curso.enums.CategoriaCurso;
import com.edutech.api.domain.curso.enums.NivelCurso;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CursoUpdateDTO(
        String nome,

        @Size(min = 10, max = 500, message = "Descrição deve ter de 10 a 500 caracteres")
        String descricao,

        @Positive(message = "Carga horaria deve ser positiva")
        Integer cargaHorariaTotal,

        @Positive(message = "Duração deve ser positiva")
        @Max(value = 120, message = "Duração não pode exceder 120 meses")
        Integer duracaoMeses,

        NivelCurso nivel,

        CategoriaCurso categoria
) {}
