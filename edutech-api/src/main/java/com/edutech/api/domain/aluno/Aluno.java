package com.edutech.api.domain.aluno;

import com.edutech.api.domain.aluno.enums.StatusAluno;
import com.edutech.api.domain.endereco.Endereco;
import com.edutech.api.domain.exception.ValidacaoException;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "tb_aluno")
@Getter
@EqualsAndHashCode(of = "id")
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Aluno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String telefone;
    private String cpf;
    private LocalDate dataDeNascimento;

    @Embedded
    private Endereco endereco;

    @Enumerated(EnumType.STRING)
    private StatusAluno status = StatusAluno.ATIVO;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Aluno() {
    }

    public Aluno(String nome,
                 String email,
                 String telefone,
                 String cpf,
                 LocalDate dataDeNascimento,
                 Endereco endereco) {
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.cpf = cpf;
        this.dataDeNascimento = dataDeNascimento;
        this.endereco = endereco;
        this.status = StatusAluno.ATIVO;
    }

    public void atualizar(String nome,
                          String email,
                          String telefone,
                          LocalDate dataDeNascimento,
                          StatusAluno status,
                          Endereco endereco) {
        if (nome != null) this.nome = nome;
        if (email != null) this.email = email;
        if (telefone != null) this.telefone = telefone;
        if (dataDeNascimento != null) this.dataDeNascimento = dataDeNascimento;
        if (status != null) this.status = status;
        if (endereco != null) this.endereco = endereco;
    }

    public void excluir() {
        if (this.status == StatusAluno.FORMADO || this.status == StatusAluno.CANCELADO || this.status == StatusAluno.INATIVO) {
            throw new ValidacaoException("Aluno formado ou ja cancelado não pode ser excluido");
        }
        this.status = StatusAluno.INATIVO;
    }
}