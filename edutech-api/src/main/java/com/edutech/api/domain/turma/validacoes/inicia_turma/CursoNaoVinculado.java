package com.edutech.api.domain.turma.validacoes.inicia_turma;

import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.turma.Turma;
import org.springframework.stereotype.Component;

@Component("cursoNaoVinculadoDesvincularCurso")
public class CursoNaoVinculado implements ValidadorIniciarTurma{

    @Override
    public void validar(Turma turma) {
        if (turma.getCurso() == null) {
            throw new ValidacaoException(
                    "Não é possível iniciar uma turma sem curso vinculado."
            );
        }
    }
}
