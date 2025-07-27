package com.edutech.api.domain.matricula.validadores;

import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.matricula.dto.MatriculaCreateDTO;
import com.edutech.api.domain.matricula.enums.StatusMatricula;
import com.edutech.api.domain.matricula.repository.MatriculaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LimiteDeTurmasPorAluno implements ValidadorCadastroMatricula{

    private final MatriculaRepository matriculaRepository;

    @Override
    public void validar(MatriculaCreateDTO dto) {
        long quantidadeAtiva = matriculaRepository
                .countByAlunoIdAndStatus(dto.alunoId(), StatusMatricula.ATIVA);

        if (quantidadeAtiva >= 3) {
            throw new ValidacaoException(
                    "O aluno ja esta matriculado em 3 turmas ativas e não pode se matricular em mais uma"
            );
        }
    }
}
