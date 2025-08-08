package com.edutech.api.domain.matricula.mapper;

import com.edutech.api.domain.matricula.Matricula;
import com.edutech.api.domain.matricula.dto.*;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface MatriculaMapper {

    /**
     * Converte entidade Matricula para DTO resumido (exibição em listagens)
     */
    @Mapping(target = "alunoId", source = "aluno.id")
    @Mapping(target = "nomeAluno", source = "aluno.nome")
    @Mapping(target = "turmaId", source = "turma.id")
    @Mapping(target = "codigoTurma", source = "turma.codigo")
    MatriculaResumoDTO toResumoDTO(Matricula matricula);

    /**
     * Converte entidade Matricula para DTO detalhado (exibição completa)
     */
    @Mapping(target = "alunoId", source = "aluno.id")
    @Mapping(target = "nomeAluno", source = "aluno.nome")
    @Mapping(target = "turmaId", source = "turma.id")
    @Mapping(target = "codigoTurma", source = "turma.codigo")
    MatriculaDetalhesDTO toDetalhesDTO(Matricula matricula);
}
