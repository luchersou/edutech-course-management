package com.edutech.api.domain.curso;

import com.edutech.api.domain.curso.enums.CategoriaCurso;
import com.edutech.api.domain.curso.enums.NivelCurso;
import com.edutech.api.domain.curso.enums.StatusCurso;
import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.matricula.Matricula;
import com.edutech.api.domain.professor.Professor;
import com.edutech.api.domain.turma.Turma;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Table(name = "tb_curso")
@Getter
@EqualsAndHashCode(of = "id")
@Entity
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String descricao;
    private Integer cargaHorariaTotal;
    private Integer duracaoMeses;

    @Enumerated(EnumType.STRING)
    private StatusCurso status = StatusCurso.ATIVO;

    @Enumerated(EnumType.STRING)
    private NivelCurso nivel;

    @Enumerated(EnumType.STRING)
    private CategoriaCurso categoria;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tb_curso_professor",
            joinColumns = @JoinColumn(name = "curso_id"),
            inverseJoinColumns = @JoinColumn(name = "professor_id")
    )
    private List<Professor> professores = new ArrayList<>();

    @OneToMany(mappedBy = "curso")
    private List<Turma> turmas = new ArrayList<>();

    public Curso() {
    }

    public Curso(String nome,
                 String descricao,
                 Integer cargaHorariaTotal,
                 Integer duracaoMeses,
                 NivelCurso nivel,
                 CategoriaCurso categoria){
        this.nome = nome;
        this.descricao = descricao;
        this.cargaHorariaTotal = cargaHorariaTotal;
        this.duracaoMeses = duracaoMeses;
        this.nivel = nivel;
        this.categoria = categoria;

        validar();
    }

    public void atualizar(String nome,
                          String descricao,
                          Integer cargaHorariaTotal,
                          Integer duracaoMeses,
                          NivelCurso nivel,
                          CategoriaCurso categoria) {
        if (nome != null) this.nome = nome;
        if (descricao != null) this.descricao = descricao;
        if (cargaHorariaTotal != null) this.cargaHorariaTotal = cargaHorariaTotal;
        if (duracaoMeses != null) this.duracaoMeses = duracaoMeses;
        if (nivel != null) this.nivel = nivel;
        if (categoria != null) this.categoria = categoria;
    }

    @PrePersist
    @PreUpdate
    private void validar() {
        if (this.nivel == null) {
            throw new ValidacaoException("Nivel do curso é obrigatório");
        }
        if (this.categoria == null) {
            throw new ValidacaoException("Categoria do curso é obrigatória");
        }
        if (duracaoMeses == null || duracaoMeses <= 0) {
            throw new ValidacaoException("Duração em meses deve ser um valor positivo");
        }
        if (cargaHorariaTotal == null || cargaHorariaTotal <= 0) {
            throw new ValidacaoException("Carga horaria deve ser positiva");
        }
        if ((nivel == NivelCurso.AVANCADO || nivel == NivelCurso.ESPECIALIZACAO) && cargaHorariaTotal < 100) {
            throw new ValidacaoException("Cursos avançados ou de especialização devem ter 100+ horas");
        }
    }

    public void ativar() {
        if (this.status == StatusCurso.ATIVO) {
            throw new ValidacaoException("Curso já está ativo.");
        }
        this.status = StatusCurso.ATIVO;
    }

    public void inativar() {
        if (this.status == StatusCurso.INATIVO) {
            throw new ValidacaoException("Curso já está inativo.");
        }
        this.status = StatusCurso.INATIVO;
    }
}
