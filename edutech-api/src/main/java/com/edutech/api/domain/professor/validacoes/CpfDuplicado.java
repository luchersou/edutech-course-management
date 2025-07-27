package com.edutech.api.domain.professor.validacoes;

import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.professor.dto.ProfessorCreateDTO;
import com.edutech.api.domain.professor.repository.ProfessorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("cpfDuplicadoProfessor")
@RequiredArgsConstructor
public class CpfDuplicado implements ValidadorCadastroProfessor {

    private final ProfessorRepository professorRepository;

    @Override
    public void validar(ProfessorCreateDTO dto) {
        if (professorRepository.existsByCpf(dto.cpf())) {
            throw new ValidacaoException(
                    "Já existe um professor com este CPF cadastrado."
            );
        }
    }
}
