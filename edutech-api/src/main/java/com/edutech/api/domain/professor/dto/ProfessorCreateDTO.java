package com.edutech.api.domain.professor.dto;

import com.edutech.api.domain.endereco.dto.DadosEnderecoDTO;
import com.edutech.api.domain.enums.Modalidade;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record ProfessorCreateDTO(
        @NotBlank
        String nome,

        @NotBlank
        @Email
        String email,

        @NotNull
        @JsonFormat(pattern = "dd/MM/yyyy")
        @Past
        LocalDate dataNascimento,

        @NotBlank
        @Pattern(regexp = "\\(\\d{2}\\)\\d{4,5}\\-\\d{4}", message = "Telefone deve ser no formato (XX)XXXXX-XXXX ou (XX)XXXX-XXXX")
        String telefone,

        @NotBlank
        @Pattern(regexp = "\\d{11}", message = "CPF deve conter 11 numeros e ser no formato XXXXXXXXXXX")
        String cpf,

        @NotNull
        Modalidade modalidade,

        @NotNull
        @Valid
        DadosEnderecoDTO endereco
) {}

