package com.edutech.api.domain.curso.dto;

import com.edutech.api.domain.curso.enums.CategoriaCurso;
import com.edutech.api.domain.curso.enums.NivelCurso;
import com.edutech.api.domain.curso.enums.StatusCurso;

import java.util.List;

public record CursoDetalhesDTO(
        Long id,
        String nome,
        String descricao,
        Integer cargaHorariaTotal,
        Integer duracaoMeses,
        StatusCurso status,
        NivelCurso nivel,
        CategoriaCurso categoria,
        List<String> nomesProfessores
) {}
