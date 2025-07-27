package com.edutech.api.domain.curso.mapper;

import com.edutech.api.domain.curso.Curso;
import com.edutech.api.domain.curso.dto.*;
import com.edutech.api.domain.professor.Professor;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CursoMapper {

    /**
     * Converte entidade Curso para DTO detalhado (exibição completa)
     */
    CursoResumoDTO toResumoDTO(Curso entity);

    /**
     * Converte entidade Curso para DTO detalhado incluindo nomes dos professores
     */
    @Mapping(target = "nomesProfessores", source = "professores")
    CursoDetalhesDTO toDetalhesDTO(Curso curso);

    default String mapProfessoresParaNomes(Professor professor){
        return professor != null ? professor.getNome() : null;
    }
}