package com.edutech.api.domain.turma.validacoes.atualiza_turma;

import com.edutech.api.domain.turma.Turma;
import com.edutech.api.domain.turma.dto.TurmaUpdateDTO;
import com.edutech.api.domain.turma.repository.TurmaRepository;
import com.edutech.api.domain.turma.validacoes.ValidadorTurmaBase;
import org.springframework.stereotype.Component;

@Component
public class ValidadorAtualizaTurmaImpl extends ValidadorTurmaBase implements ValidadorAtualizaTurma {

    public ValidadorAtualizaTurmaImpl(TurmaRepository turmaRepository) {
        super(turmaRepository);
    }

    @Override
    public void validar(Turma turmaId, TurmaUpdateDTO dto) {
        validarCodigoUnicoParaAtualizacao(turmaId.getId(), dto.codigo());
        validarDuracaoMinima(dto.dataInicio(), dto.dataFim());
        validarDuracaoAula(dto.horarioInicio(), dto.horarioFim());
        validarHorarioFuncionamento(dto.horarioInicio(), dto.horarioFim());
        validarPeriodoLetivo(dto.dataInicio(), dto.dataFim());
    }
}
