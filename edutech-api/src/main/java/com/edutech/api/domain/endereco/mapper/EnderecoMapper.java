package com.edutech.api.domain.endereco.mapper;

import com.edutech.api.domain.endereco.Endereco;
import com.edutech.api.domain.endereco.dto.DadosEnderecoDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EnderecoMapper {

    Endereco toEndereco(DadosEnderecoDTO dto);
}
