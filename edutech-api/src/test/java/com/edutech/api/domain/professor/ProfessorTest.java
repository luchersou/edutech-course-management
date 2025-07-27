package com.edutech.api.domain.professor;

import com.edutech.api.domain.endereco.Endereco;
import com.edutech.api.domain.enums.Modalidade;
import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.professor.enums.StatusProfessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários da entidade Professor")
class ProfessorTest {

    private Endereco endereco;
    private Professor professor;

    @BeforeEach
    void setup() {
        endereco = new Endereco(
                "Rua das Acácias", "Centro", "12345678",
                "São Paulo", "SP", "Apto 101", "100"
        );

        professor = new Professor(
                "Carlos",
                "carlos@email.com",
                LocalDate.of(1980, 1, 1),
                "(11)99999-8888",
                "12345678900",
                Modalidade.EAD,
                endereco
        );
    }

    @Test
    @DisplayName("Sucesso na instância: Deve criar uma nova instância de professor com dados válidos")
    void deveInstanciarProfessorComSucesso() {
        assertAll(
                () -> assertEquals("Carlos", professor.getNome()),
                () -> assertEquals("carlos@email.com", professor.getEmail()),
                () -> assertEquals(LocalDate.of(1980, 1,1), professor.getDataNascimento()),
                () -> assertEquals("(11)99999-8888", professor.getTelefone()),
                () -> assertEquals("12345678900", professor.getCpf()),
                () -> assertEquals(Modalidade.EAD, professor.getModalidade()),
                () -> assertEquals(StatusProfessor.ATIVO, professor.getStatus()),
                () -> assertEquals(endereco, professor.getEndereco())
        );
    }

    @Test
    @DisplayName("Sucesso na atualização: Deve permitir a modificação de múltiplos campos com sucesso")
    void deveAtualizarCamposComSucesso() {
        var novoEndereco = new Endereco(
                "Av Brasil", "Copacabana", "22021001",
                "Rio de Janeiro", "RJ", "Bloco B", "150"
        );

        professor.atualizar(
                "Novo Nome",
                "novo@email.com",
                LocalDate.of(1990, 2, 2),
                "888888888",
                StatusProfessor.AFASTADO,
                Modalidade.PRESENCIAL,
                novoEndereco
        );

        assertAll(
                () -> assertEquals("Novo Nome", professor.getNome()),
                () -> assertEquals("novo@email.com", professor.getEmail()),
                () -> assertEquals(LocalDate.of(1990, 2, 2), professor.getDataNascimento()),
                () -> assertEquals("888888888", professor.getTelefone()),
                () -> assertEquals(StatusProfessor.AFASTADO, professor.getStatus()),
                () -> assertEquals(Modalidade.PRESENCIAL, professor.getModalidade()),
                () -> assertEquals(novoEndereco, professor.getEndereco())
        );
    }

    @Test
    @DisplayName("Comportamento na atualização: Deve ignorar campos que forem passados como nulos durante a atualização")
    void deveIgnorarCamposNulosNaAtualizacao() {
        professor.atualizar(null, null, null, null, null, null, null);

        assertAll(
                () -> assertEquals("Carlos", professor.getNome()),
                () -> assertEquals("carlos@email.com", professor.getEmail()),
                () -> assertEquals(LocalDate.of(1980, 1, 1), professor.getDataNascimento()),
                () -> assertEquals("(11)99999-8888", professor.getTelefone()),
                () -> assertEquals(StatusProfessor.ATIVO, professor.getStatus()),
                () -> assertEquals(Modalidade.EAD, professor.getModalidade()),
                () -> assertEquals(endereco, professor.getEndereco())
        );
    }

    @Test
    @DisplayName("Comportamento ao excluir: Deve alterar o status para 'Inativo' quando o registro for excluído")
    void deveAlterarStatusParaInativoAoExcluir() {
        professor.excluir();

        assertEquals(StatusProfessor.INATIVO, professor.getStatus());
    }

    @Test
    @DisplayName("Falha na exclusão: Deve lançar exceção ao tentar excluir um professor com status 'Afastado'")
    void deveLancarExcecaoAoExcluirProfessorAfastado() {
        professor.atualizar(null, null, null, null, StatusProfessor.AFASTADO, null, null);

        var ex = assertThrows(ValidacaoException.class, professor::excluir);
        assertTrue(ex.getMessage().contains("afastado ou inativo"));
    }

    @Test
    @DisplayName("Falha na exclusão: Deve lançar exceção ao tentar excluir um professor com status 'Inativo'")
    void deveLancarExcecaoAoExcluirProfessorInativo() {
        professor.atualizar(null, null, null, null, StatusProfessor.INATIVO, null, null);

        var ex = assertThrows(ValidacaoException.class, professor::excluir);
        assertTrue(ex.getMessage().contains("Professor afastado ou inativo não pode ser cancelado"));
    }
}
