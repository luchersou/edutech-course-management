package com.edutech.api.domain.matricula.validadores;

import com.edutech.api.domain.aluno.repository.AlunoRepository;
import com.edutech.api.domain.aluno.enums.StatusAluno;
import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.matricula.dto.MatriculaCreateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AlunoExistenteAtivo implements ValidadorCadastroMatricula {

    private final AlunoRepository alunoRepository;

    @Override
    public void validar(MatriculaCreateDTO dto) {
        var aluno = alunoRepository.findById(dto.alunoId())
                .orElseThrow(() -> new ValidacaoException(
                        "Aluno com ID " + dto.alunoId() + " não encontrado"
                ));

        if (aluno.getStatus() != StatusAluno.ATIVO) {
            throw new ValidacaoException(
                    "Aluno inativo para matricula"
            );
        }
    }
}
