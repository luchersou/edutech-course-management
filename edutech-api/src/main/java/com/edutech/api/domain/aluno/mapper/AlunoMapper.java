package com.edutech.api.domain.aluno.mapper;

import com.edutech.api.domain.aluno.Aluno;
import com.edutech.api.domain.aluno.dto.AlunoDetalhesDTO;
import com.edutech.api.domain.aluno.dto.AlunoResumoDTO;
import com.edutech.api.domain.endereco.mapper.EnderecoMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = EnderecoMapper.class)
public interface AlunoMapper {

    /**
     * Converte entidade Aluno para DTO resumido (exibição em listagens)
     */
    AlunoResumoDTO toResumoDTO(Aluno aluno);

    /**
     * Converte entidade Aluno para DTO detalhado (exibição completa)
     */
    AlunoDetalhesDTO toDetalhesDTO(Aluno aluno);
}
