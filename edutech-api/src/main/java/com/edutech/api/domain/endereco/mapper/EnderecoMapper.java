package com.edutech.api.domain.endereco.mapper;

import com.edutech.api.domain.endereco.Endereco;
import com.edutech.api.domain.endereco.dto.DadosEnderecoDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EnderecoMapper {

    default Endereco toEndereco(DadosEnderecoDTO dto) {
        if (dto == null) {
            return null;
        }

        return new Endereco(
                dto.logradouro(),
                dto.bairro(),
                dto.cep(),
                dto.numero(),
                dto.complemento(),
                dto.cidade(),
                dto.uf()
        );
    }
}
