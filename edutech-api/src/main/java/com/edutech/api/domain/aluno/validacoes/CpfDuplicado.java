package com.edutech.api.domain.aluno.validacoes;

import com.edutech.api.domain.aluno.dto.AlunoCreateDTO;
import com.edutech.api.domain.aluno.repository.AlunoRepository;
import com.edutech.api.domain.exception.ValidacaoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("cpfDuplicadoAluno")
@RequiredArgsConstructor
public class CpfDuplicado implements ValidadorCadastroAluno {

    private final AlunoRepository alunoRepository;

    @Override
    public void validar(AlunoCreateDTO dto) {
        if(alunoRepository.existsByCpf(dto.cpf())){
            throw new ValidacaoException(
                    "Já existe um aluno com este CPF cadastrado."
            );
        }
    }
}
