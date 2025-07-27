package com.edutech.api.domain.turma.validacoes.cadastra_turma;

import com.edutech.api.domain.turma.dto.TurmaCreateDTO;
import com.edutech.api.domain.turma.repository.TurmaRepository;
import com.edutech.api.domain.turma.validacoes.ValidadorTurmaBase;
import org.springframework.stereotype.Component;

@Component
public class ValidadorCadastroTurmaImpl extends ValidadorTurmaBase implements ValidadorCadastroTurma{

    public ValidadorCadastroTurmaImpl(TurmaRepository turmaRepository) {
        super(turmaRepository);
    }

    @Override
    public void validar(TurmaCreateDTO dto) {
        validarCodigoUnico(dto.codigo());
        validarDuracaoMinima(dto.dataInicio(), dto.dataFim());
        validarDuracaoAula(dto.horarioInicio(), dto.horarioFim());
        validarHorarioFuncionamento(dto.horarioInicio(), dto.horarioFim());
        validarPeriodoLetivo(dto.dataInicio(), dto.dataFim());
    }
}
