package com.edutech.api.domain.turma.mapper;

import com.edutech.api.domain.matricula.Matricula;
import com.edutech.api.domain.turma.Turma;
import com.edutech.api.domain.turma.dto.TurmaComMatriculasDTO;
import com.edutech.api.domain.turma.dto.TurmaDetalhesDTO;
import com.edutech.api.domain.turma.dto.TurmaResumoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TurmaMapper {

    /**
     * Converte entidade Turma para DTO resumido (exibição em listagens)
     */
    TurmaResumoDTO toResumoDTO(Turma turma);

    /**
     * Converte entidade Turma para DTO detalhado (exibição completa)
     */
    TurmaDetalhesDTO toDetalhesDTO(Turma turma);

    @Mapping(target = "professorId", source = "professor.id")
    @Mapping(target = "cursoId", source = "curso.id")
    @Mapping(target = "matriculasIds", source = "matriculas")
    TurmaComMatriculasDTO toTurmaComMatriculasDTO(Turma turma);

    default Long mapMatriculasParaIds(Matricula matricula) {
        return matricula != null ? matricula.getId() : null;
    }
}

