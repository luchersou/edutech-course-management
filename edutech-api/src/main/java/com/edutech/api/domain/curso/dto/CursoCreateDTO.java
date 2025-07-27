package com.edutech.api.domain.curso.dto;

import com.edutech.api.domain.curso.enums.CategoriaCurso;
import com.edutech.api.domain.curso.enums.NivelCurso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CursoCreateDTO(
        @NotBlank(message = "O nome do curso é obrigatório")
        String nome,

        @NotBlank(message = "A descrição é obrigatória")
        @Size(min = 10, max = 500, message = "A descrição deve ter entre 10 e 500 caracteres")
        String descricao,

        @NotNull(message = "A carga horaria é obrigatória")
        @Positive(message = "A carga horaria deve ser positiva")
        Integer cargaHorariaTotal,

        @NotNull(message = "A duração em meses é obrigatória")
        @Positive(message = "A duração deve ser maior que zero")
        Integer duracaoMeses,

        @NotNull(message = "O nivel do curso é obrigatório")
        NivelCurso nivel,

        @NotNull(message = "A categoria do curso é obrigatória")
        CategoriaCurso categoria
) {}
