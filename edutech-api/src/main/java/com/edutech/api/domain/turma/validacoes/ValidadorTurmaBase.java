package com.edutech.api.domain.turma.validacoes;

import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.turma.repository.TurmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;

@Component
@RequiredArgsConstructor
public abstract class ValidadorTurmaBase {

    protected final TurmaRepository turmaRepository;

    protected void validarCodigoUnico(String codigo) {
        if (codigo != null && turmaRepository.existsByCodigo(codigo)) {
            throw new ValidacaoException(
                    "Já existe uma turma com este código"
            );
        }
    }

    protected void validarCodigoUnicoParaAtualizacao(Long turmaId, String codigo) {
        if (codigo != null && turmaRepository.existsByCodigoAndIdNot(codigo, turmaId)) {
            throw new ValidacaoException(
                    "Já existe uma turma com este código"
            );
        }
    }

    protected void validarDuracaoMinima(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio != null && dataFim != null) {
            if (ChronoUnit.DAYS.between(dataInicio, dataFim) < 30) {
                throw new ValidacaoException(
                        "Turma deve ter duração mínima de 30 dias"
                );
            }
        }
    }

    protected void validarDuracaoAula(LocalTime horarioInicio, LocalTime horarioFim) {
        if (horarioInicio != null && horarioFim != null) {
            Duration duracao = Duration.between(horarioInicio, horarioFim);

            if (duracao.toMinutes() < 60) {
                throw new ValidacaoException(
                        "Aula deve ter duração mínima de 1 hora"
                );
            }

            if (duracao.toHours() > 8) {
                throw new ValidacaoException(
                        "Aula não pode exceder 8 horas"
                );
            }
        }
    }

    protected void validarHorarioFuncionamento(LocalTime horarioInicio, LocalTime horarioFim) {
        if (horarioInicio != null && horarioFim != null) {
            LocalTime inicioExpediente = LocalTime.of(6, 0);
            LocalTime fimExpediente = LocalTime.of(22, 0);

            if (horarioInicio.isBefore(inicioExpediente) || horarioFim.isAfter(fimExpediente)) {
                throw new ValidacaoException(
                        "Horario deve estar entre 06:00 e 22:00"
                );
            }

            if (horarioFim.isBefore(horarioInicio) || horarioFim.equals(horarioInicio)) {
                throw new ValidacaoException(
                        "Horario de fim deve ser posterior ao horario de inicio"
                );
            }
        }
    }

    protected void validarPeriodoLetivo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio != null && dataFim != null) {
            Month mesInicio = dataInicio.getMonth();
            Month mesFim = dataFim.getMonth();

            if (mesInicio == Month.DECEMBER || mesInicio == Month.JANUARY) {
                throw new ValidacaoException(
                        "Turma não pode iniciar em dezembro ou janeiro"
                );
            }

            if (mesFim == Month.DECEMBER || mesFim == Month.JANUARY) {
                throw new ValidacaoException(
                        "Turma não pode terminar em dezembro ou janeiro"
                );
            }
        }
    }
}