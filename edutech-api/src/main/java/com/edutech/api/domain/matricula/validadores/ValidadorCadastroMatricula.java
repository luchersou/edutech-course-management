package com.edutech.api.domain.matricula.validadores;

import com.edutech.api.domain.matricula.dto.MatriculaCreateDTO;

public interface ValidadorCadastroMatricula {
    void validar(MatriculaCreateDTO dto);
}