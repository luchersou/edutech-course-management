package com.edutech.api.domain.professor.dto;

import com.edutech.api.domain.endereco.dto.DadosEnderecoDTO;
import com.edutech.api.domain.enums.Modalidade;
import com.edutech.api.domain.professor.enums.StatusProfessor;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record ProfessorDetalhesDTO(
        Long id,
        String nome,
        String email,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataNascimento,
        String telefone,
        Modalidade modalidade,
        StatusProfessor status,
        DadosEnderecoDTO endereco
) {}
