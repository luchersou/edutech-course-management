package com.edutech.api.domain.curso;

import com.edutech.api.domain.curso.enums.CategoriaCurso;
import com.edutech.api.domain.curso.enums.NivelCurso;
import com.edutech.api.domain.curso.enums.StatusCurso;
import com.edutech.api.domain.exception.ValidacaoException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários da entidade Curso")
class CursoTest {

    @Test
    @DisplayName("Deve criar curso válido com sucesso")
    void deveCriarCursoValido() {
        Curso curso = new Curso(
                "Curso Java",
                "Curso completo de Java",
                120,
                6,
                NivelCurso.AVANCADO,
                CategoriaCurso.PROGRAMACAO
        );

        assertAll(
                () -> assertEquals("Curso Java", curso.getNome()),
                () -> assertEquals(StatusCurso.ATIVO, curso.getStatus()),
                () -> assertEquals(NivelCurso.AVANCADO, curso.getNivel())
        );
    }

    @Test
    @DisplayName("Deve lançar exceção se nivel for nulo")
    void deveLancarExcecaoNivelNulo() {
        ValidacaoException ex = assertThrows(ValidacaoException.class, () -> {
            new Curso("Curso Java", "Descrição", 120, 6, null, CategoriaCurso.PROGRAMACAO);
        });

        assertEquals("Nivel do curso é obrigatório", ex.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção se carga horaria for menor que 100 para curso avançado")
    void deveLancarExcecaoCargaHorariaInsuficiente() {
        ValidacaoException ex = assertThrows(ValidacaoException.class, () -> {
            new Curso("Curso Java", "Descrição", 90, 6, NivelCurso.AVANCADO, CategoriaCurso.PROGRAMACAO);
        });

        assertEquals("Cursos avançados ou de especialização devem ter 100+ horas", ex.getMessage());
    }

    @Test
    @DisplayName("Deve inativar curso ativo")
    void deveInativarCurso() {
        Curso curso = new Curso("Nome", "Desc", 120, 6, NivelCurso.BASICO, CategoriaCurso.PROGRAMACAO);

        curso.inativar();

        assertEquals(StatusCurso.INATIVO, curso.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção se tentar inativar curso já inativo")
    void deveLancarExcecaoInativarCursoInativo() {
        Curso curso = new Curso("Nome", "Desc", 120, 6, NivelCurso.BASICO, CategoriaCurso.PROGRAMACAO);
        curso.inativar();

        ValidacaoException ex = assertThrows(ValidacaoException.class, curso::inativar);

        assertEquals("Curso já está inativo.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve ativar curso inativo")
    void deveAtivarCurso() {
        Curso curso = new Curso("Nome", "Desc", 120, 6, NivelCurso.BASICO, CategoriaCurso.PROGRAMACAO);
        curso.inativar();
        curso.ativar();

        assertEquals(StatusCurso.ATIVO, curso.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção ao ativar curso já ativo")
    void deveLancarExcecaoAtivarCursoAtivo() {
        Curso curso = new Curso("Nome", "Desc", 120, 6, NivelCurso.BASICO, CategoriaCurso.PROGRAMACAO);

        ValidacaoException ex = assertThrows(ValidacaoException.class, curso::ativar);

        assertEquals("Curso já está ativo.", ex.getMessage());
    }

    @Test
    @DisplayName("Deve atualizar os dados de curso com valores não nulos")
    void deveAtualizarCurso() {
        Curso curso = new Curso("Nome", "Desc", 100, 5, NivelCurso.BASICO, CategoriaCurso.PROGRAMACAO);

        curso.atualizar("Novo Nome", null, 200, 8, NivelCurso.AVANCADO, null);

        assertAll(
                () -> assertEquals("Novo Nome", curso.getNome()),
                () -> assertEquals(200, curso.getCargaHorariaTotal()),
                () -> assertEquals(8, curso.getDuracaoMeses()),
                () -> assertEquals(NivelCurso.AVANCADO, curso.getNivel()),
                () -> assertEquals("Desc", curso.getDescricao()),
                () -> assertEquals(CategoriaCurso.PROGRAMACAO, curso.getCategoria())
        );
    }
}

