package com.edutech.api.domain.matricula;

import com.edutech.api.domain.aluno.Aluno;
import com.edutech.api.domain.curso.Curso;
import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.matricula.enums.MotivoCancelamento;
import com.edutech.api.domain.matricula.enums.StatusMatricula;
import com.edutech.api.domain.turma.Turma;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Table(name = "tb_matricula")
@Getter
@EqualsAndHashCode(of = "id")
@Entity
public class Matricula {

    private static final BigDecimal NOTA_MINIMA_PARA_CONCLUSAO = new BigDecimal("7.0");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate dataMatricula;
    private LocalDate dataConclusao;
    private BigDecimal notaFinal;

    @Enumerated(EnumType.STRING)
    private StatusMatricula status = StatusMatricula.ATIVA;

    @Enumerated(EnumType.STRING)
    private MotivoCancelamento motivoCancelamento;

    @ManyToOne
    @JoinColumn(name = "aluno_id")
    private Aluno aluno;

    @ManyToOne
    @JoinColumn(name = "turma_id")
    private Turma turma;

    public Matricula() {
    }

    public Matricula(Aluno aluno, Turma turma, LocalDate dataMatricula) {
        if (aluno == null) {
            throw new ValidacaoException("Aluno é obrigatório para a matricula.");
        }
        if (turma == null) {
            throw new ValidacaoException("Turma é obrigatório para a matricula.");
        }
        if (dataMatricula == null) {
            throw new ValidacaoException("Data de matricula é obrigatória.");
        }

        this.aluno = aluno;
        this.turma = turma;
        this.dataMatricula = dataMatricula;

        this.status = StatusMatricula.ATIVA;
        this.dataConclusao = null;
        this.notaFinal = null;
        this.motivoCancelamento = null;

        validar();
    }

    public void cancelar(MotivoCancelamento motivo) {
        if (this.status == StatusMatricula.CONCLUIDA) {
            throw new ValidacaoException("Matricula concluída não pode ser cancelada");
        }
        if (this.status == StatusMatricula.CANCELADA) {
            throw new ValidacaoException("Matricula já está cancelada");
        }
        if (motivo == null) {
            throw new ValidacaoException("Motivo do cancelamento é obrigatório");
        }
        this.motivoCancelamento = motivo;
        this.status = StatusMatricula.CANCELADA;

        validar();
    }

    public void concluir(BigDecimal nota) {
        if (this.status != StatusMatricula.ATIVA) {
            throw new ValidacaoException("Apenas matrículas ativas podem ser concluídas");
        }
        if (nota == null) {
            throw new ValidacaoException("Nota final é obrigatória para conclusão");
        }
        this.dataConclusao = LocalDate.now();
        this.notaFinal = nota;
        this.status = StatusMatricula.CONCLUIDA;

        validar();
    }

    public void trancar() {
        if (this.status != StatusMatricula.ATIVA) {
            throw new ValidacaoException("Apenas matriculas ativas podem ser trancadas");
        }
        this.status = StatusMatricula.TRANCADA;

        validar();
    }

    public void reativar() {
        if (this.status != StatusMatricula.TRANCADA) {
            throw new ValidacaoException("Apenas matrículas trancadas podem ser reativadas");
        }
        this.status = StatusMatricula.ATIVA;

        validar();
    }

    @PrePersist
    @PreUpdate
    private void validar() {
        if (dataConclusao != null && dataMatricula != null &&
                dataConclusao.isBefore(dataMatricula)) {
            throw new ValidacaoException("Data de conclusão invalida");
        }

        if (status == StatusMatricula.CONCLUIDA) {
            if (notaFinal == null || notaFinal.compareTo(NOTA_MINIMA_PARA_CONCLUSAO) < 0) {
                throw new ValidacaoException("Matricula concluida requer nota >= 7");
            }
        }
    }
}
