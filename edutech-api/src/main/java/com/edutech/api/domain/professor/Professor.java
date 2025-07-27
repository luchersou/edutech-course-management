package com.edutech.api.domain.professor;

import com.edutech.api.domain.endereco.Endereco;
import com.edutech.api.domain.enums.Modalidade;
import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.professor.enums.StatusProfessor;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Table(name = "tb_professor")
@Getter
@EqualsAndHashCode(of = "id")
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Professor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private LocalDate dataNascimento;
    private String telefone;
    private String cpf;

    @Enumerated(EnumType.STRING)
    private StatusProfessor status = StatusProfessor.ATIVO;

    @Enumerated(EnumType.STRING)
    private Modalidade modalidade;

    @Embedded
    private Endereco endereco;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Professor() {}

    public Professor(String nome,
                     String email,
                     LocalDate dataNascimento,
                     String telefone,
                     String cpf,
                     Modalidade modalidade,
                     Endereco endereco) {
        this.nome = nome;
        this.email = email;
        this.dataNascimento = dataNascimento;
        this.telefone = telefone;
        this.cpf = cpf;
        this.modalidade = modalidade;
        this.endereco = endereco;
        this.status = StatusProfessor.ATIVO;
    }

    public void atualizar(String nome,
                          String email,
                          LocalDate dataNascimento,
                          String telefone,
                          StatusProfessor status,
                          Modalidade modalidade,
                          Endereco endereco) {
        if (nome != null) this.nome = nome;
        if (email != null) this.email = email;
        if (dataNascimento != null) this.dataNascimento = dataNascimento;
        if (telefone != null) this.telefone = telefone;
        if (status != null) this.status = status;
        if (modalidade != null) this.modalidade = modalidade;
        if (endereco != null) this.endereco = endereco;
    }

    public void excluir() {
        if (this.status == StatusProfessor.AFASTADO || this.status == StatusProfessor.INATIVO) {
            throw new ValidacaoException("Professor afastado ou inativo não pode ser cancelado");
        }
        this.status = StatusProfessor.INATIVO;
    }
}
