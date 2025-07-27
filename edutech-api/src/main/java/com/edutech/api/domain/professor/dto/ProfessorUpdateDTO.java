package com.edutech.api.domain.professor.dto;

import com.edutech.api.domain.endereco.dto.DadosEnderecoDTO;
import com.edutech.api.domain.enums.Modalidade;
import com.edutech.api.domain.professor.enums.StatusProfessor;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record ProfessorUpdateDTO(
        String nome,

        @Email
        String email,

        @Past
        @JsonFormat(pattern = "dd/MM/yyyy")
        LocalDate dataNascimento,

        @Pattern(regexp = "\\(\\d{2}\\)\\d{4,5}\\-\\d{4}", message = "Telefone deve ser no formato (XX)XXXXX-XXXX ou (XX)XXXX-XXXX")
        String telefone,

        StatusProfessor status,

        Modalidade modalidade,

        @Valid
        DadosEnderecoDTO endereco
) {}
