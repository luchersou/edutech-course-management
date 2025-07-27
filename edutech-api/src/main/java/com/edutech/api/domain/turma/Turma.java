package com.edutech.api.domain.turma;

import com.edutech.api.domain.curso.Curso;
import com.edutech.api.domain.enums.Modalidade;
import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.matricula.Matricula;
import com.edutech.api.domain.professor.Professor;
import com.edutech.api.domain.turma.enums.StatusTurma;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Table(name = "tb_turma")
@Getter
@EqualsAndHashCode(of = "id")
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String codigo;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private LocalTime horarioInicio;
    private LocalTime horarioFim;
    private Integer vagasTotais;

    @Enumerated(EnumType.STRING)
    private StatusTurma status = StatusTurma.ABERTA;

    @Enumerated(EnumType.STRING)
    private Modalidade modalidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curso_id")
    private Curso curso;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id")
    private Professor professor;

    @OneToMany(mappedBy = "turma", cascade = CascadeType.ALL)
    private List<Matricula> matriculas = new ArrayList<>();

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Turma() {
    }

    public Turma(String codigo,
                 LocalDate dataInicio,
                 LocalDate dataFim,
                 LocalTime horarioInicio,
                 LocalTime horarioFim,
                 Integer vagasTotais,
                 Modalidade modalidade) {
        this.codigo = codigo;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.horarioInicio = horarioInicio;
        this.horarioFim = horarioFim;
        this.vagasTotais = vagasTotais;
        this.modalidade = modalidade;
        this.status = StatusTurma.ABERTA;

        validarDados();
    }

    public void atualizar(String codigo,
                          LocalDate dataInicio,
                          LocalDate dataFim,
                          LocalTime horarioInicio,
                          LocalTime horarioFim,
                          Integer vagasTotais,
                          Modalidade modalidade) {
        if (codigo != null) this.codigo = codigo;
        if (dataInicio != null) this.dataInicio = dataInicio;
        if (dataFim != null) this.dataFim = dataFim;
        if (horarioInicio != null) this.horarioInicio = horarioInicio;
        if (horarioFim != null) this.horarioFim = horarioFim;
        if (vagasTotais != null) this.vagasTotais = vagasTotais;
        if (modalidade != null) this.modalidade = modalidade;

        validarDados();
    }

    public void iniciar() {
        if (this.status != StatusTurma.ABERTA) {
            throw new ValidacaoException("Só é possível iniciar turmas com status ABERTA.");
        }
        if (LocalDate.now().isBefore(dataInicio)) {
            throw new ValidacaoException("Não é possível iniciar uma turma antes da data de início.");
        }
        this.status = StatusTurma.EM_ANDAMENTO;
    }

    public void concluir() {
        if (this.status != StatusTurma.EM_ANDAMENTO) {
            throw new ValidacaoException("Só é possível concluir turmas EM_ANDAMENTO.");
        }
        if (LocalDate.now().isBefore(dataFim)) {
            throw new ValidacaoException("Não é possível concluir a turma antes da data fim.");
        }
        this.status = StatusTurma.CONCLUIDA;
    }

    public void cancelar() {
        if (this.status == StatusTurma.CONCLUIDA) {
            throw new ValidacaoException("Não é possivel cancelar uma turma já concluída.");
        }
        if (this.status == StatusTurma.CANCELADA) {
            throw new ValidacaoException("Turma já esta cancelada");
        }
        this.status = StatusTurma.CANCELADA;
    }

    @PrePersist
    @PreUpdate
    private void validarDados() {
        if (dataInicio != null && dataFim != null && dataFim.isBefore(dataInicio)) {
            throw new ValidacaoException("Data de fim deve ser posterior à data de início");
        }
        if (horarioInicio != null && horarioFim != null && !horarioFim.isAfter(horarioInicio)) {
            throw new ValidacaoException("Horario de fim deve ser posterior ao horario de inicio");
        }
        if (vagasTotais == null || vagasTotais <= 0) {
            throw new ValidacaoException("Vagas devem ser positivas");
        }
    }

    public Integer getVagasDisponiveis() {
        return vagasTotais - matriculas.size();
    }

    public void vincularProfessor(Professor professor) {
        this.professor = professor;
    }

    public void desvincularProfessor() {
        this.professor = null;
    }

    public void vincularCurso(Curso curso) {
        this.curso = curso;
    }

    public void desvincularCurso() {
        this.curso = null;
    }
}