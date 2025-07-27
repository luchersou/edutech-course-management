package com.edutech.api.domain.aluno.dto;

import com.edutech.api.domain.aluno.enums.StatusAluno;
import com.edutech.api.domain.endereco.dto.DadosEnderecoDTO;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record AlunoDetalhesDTO(
        Long id,
        String nome,
        String email,
        String telefone,
        String cpf,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataDeNascimento,
        StatusAluno status,
        DadosEnderecoDTO endereco
) {}
