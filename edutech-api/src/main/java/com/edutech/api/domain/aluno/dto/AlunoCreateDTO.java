package com.edutech.api.domain.aluno.dto;

import com.edutech.api.domain.endereco.dto.DadosEnderecoDTO;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record AlunoCreateDTO(
        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "Email é obrigatório")
        @Email
        String email,

        @Pattern(regexp = "\\(\\d{2}\\)\\d{4,5}\\-\\d{4}", message = "Telefone deve ser no formato (XX)XXXXX-XXXX ou (XX)XXXX-XXXX")
        @NotBlank(message = "Telefone é obrigatório")
        String telefone,

        @NotBlank
        @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 numeros e ser no formato XXXXXXXXXXX")
        String cpf,

        @NotNull
        @Past
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataDeNascimento,

        @Valid
        @NotNull(message = "Endereço é obrigatório")
        DadosEnderecoDTO endereco
) {}
