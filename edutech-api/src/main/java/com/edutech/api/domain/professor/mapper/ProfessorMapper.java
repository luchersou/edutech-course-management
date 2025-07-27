package com.edutech.api.domain.professor.mapper;

import com.edutech.api.domain.endereco.mapper.EnderecoMapper;
import com.edutech.api.domain.professor.Professor;
import com.edutech.api.domain.professor.dto.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = EnderecoMapper.class)
public interface ProfessorMapper {

    /**
     * Converte entidade Professor para DTO resumido (exibição em listagens)
     */
    ProfessorResumoDTO toResumoDTO(Professor professor);

    /**
     * Converte entidade Professor para DTO detalhado (exibição completa)
     */
    ProfessorDetalhesDTO toDetalhesDTO(Professor professor);
}
