package com.edutech.api.domain.aluno;

import com.edutech.api.domain.aluno.enums.StatusAluno;
import com.edutech.api.domain.endereco.Endereco;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários da entidade Aluno")
class AlunoTest {

    @Test
    @DisplayName("Deve atualizar todos os campos do aluno com os novos dados")
    void deveAtualizarCamposDoAluno() {
        Endereco enderecoOriginal = new Endereco(
                "Rua A",
                "Bairro A",
                "12345678",
                "10",
                "Ap 1",
                "Cidade A",
                "PR");

        Aluno aluno = new Aluno(
                "João",
                "joao@email.com",
                "999999999",
                "12345678900",
                LocalDate.of(1990, 1, 1),
                enderecoOriginal);

        Endereco novoEndereco = new Endereco(
                "Rua B",
                "Bairro B",
                "87654321",
                "20",
                "Casa",
                "Cidade B",
                "PR");

        aluno.atualizar("João da Silva", "joaosilva@email.com", "888888888", LocalDate.of(1991, 2, 2), StatusAluno.CANCELADO, novoEndereco);

        assertAll(
                () -> assertEquals("João da Silva", aluno.getNome()),
                () -> assertEquals("joaosilva@email.com", aluno.getEmail()),
                () -> assertEquals("888888888", aluno.getTelefone()),
                () -> assertEquals(LocalDate.of(1991, 2, 2), aluno.getDataDeNascimento()),
                () -> assertEquals(StatusAluno.CANCELADO, aluno.getStatus()),
                () -> assertEquals("Rua B", aluno.getEndereco().getLogradouro())
        );
    }

    @Test
    @DisplayName("Deve inativar o aluno quando o status atual permite a exclusão")
    void deveInativarAlunoQuandoStatusPermite() {
        Aluno aluno = new Aluno(
                "Maria",
                "maria@email.com",
                "999999999",
                "98765432100",
                LocalDate.of(1995, 5, 5),
                null);

        aluno.excluir();

        assertEquals(StatusAluno.INATIVO, aluno.getStatus());
    }
}
