package com.edutech.api.domain.curso.dto;

import com.edutech.api.domain.curso.enums.CategoriaCurso;
import com.edutech.api.domain.curso.enums.NivelCurso;
import com.edutech.api.domain.curso.enums.StatusCurso;

public record CursoResumoDTO(
        Long id,
        String nome,
        StatusCurso status,
        Integer cargaHorariaTotal,
        NivelCurso nivel,
        CategoriaCurso categoria
) {}
