package com.edutech.api.domain.aluno.dto;

import com.edutech.api.domain.aluno.enums.StatusAluno;
import com.edutech.api.domain.endereco.dto.DadosEnderecoDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record AlunoUpdateDTO(
        String nome,
        @Email
        String email,
        @Pattern(regexp = "\\(\\d{2}\\)\\d{4,5}\\-\\d{4}", message = "Telefone deve ser no formato (XX)XXXXX-XXXX ou (XX)XXXX-XXXX")
        String telefone,
        StatusAluno status,
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataDeNascimento,
        @Valid
        DadosEnderecoDTO endereco
) {}
