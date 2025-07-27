package com.edutech.api.domain.matricula;

import com.edutech.api.domain.aluno.Aluno;
import com.edutech.api.domain.curso.Curso;
import com.edutech.api.domain.curso.enums.CategoriaCurso;
import com.edutech.api.domain.curso.enums.NivelCurso;
import com.edutech.api.domain.endereco.Endereco;
import com.edutech.api.domain.enums.Modalidade;
import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.matricula.enums.MotivoCancelamento;
import com.edutech.api.domain.matricula.enums.StatusMatricula;
import com.edutech.api.domain.turma.Turma;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários da entidade Matricula")
class MatriculaTest {

    private Endereco endereco;
    private Aluno aluno;
    private Curso curso;
    private Turma turma;
    private LocalDate dataMatricula;

    @BeforeEach
    void setUp() {
        endereco = new Endereco(
                "Avenida Brasil", "Jardim América", "54321-000", "2000",
                "Bloco B", "Rio de Janeiro", "RJ"
        );

        aluno = new Aluno(
                "Maria Oliveira", "maria.oliveira@email.com", "(21) 91234-5678",
                "987.654.321-00", LocalDate.of(1985, 10, 22),
                endereco
        );

        curso = new Curso(
                "Desenvolvimento Web Full Stack", "Curso completo de desenvolvimento web com React e Spring Boot",
                240, 6, NivelCurso.INTERMEDIARIO, CategoriaCurso.PROGRAMACAO
        );

        turma = new Turma(
                "TURMA-2024-03", LocalDate.of(2024, 3, 1), LocalDate.of(2024, 12, 15),
                LocalTime.of(8, 0), LocalTime.of(9, 30), 15,
                Modalidade.EAD
        );

        dataMatricula = LocalDate.of(2025,5,20);
    }

    @Test
    @DisplayName("Sucesso na criação: Deve permitir o registro de uma nova matrícula com dados válidos")
    void deveCriarMatriculaValida() {
        var matricula = new Matricula(aluno, curso, turma, dataMatricula);

        assertEquals(StatusMatricula.ATIVA, matricula.getStatus());
        assertEquals(aluno, matricula.getAluno());
        assertEquals(curso, matricula.getCurso());
        assertEquals(turma, matricula.getTurma());
        assertEquals(dataMatricula, matricula.getDataMatricula());
        assertNull(matricula.getNotaFinal());
        assertNull(matricula.getDataConclusao());
        assertNull(matricula.getMotivoCancelamento());
    }

    @Test
    @DisplayName("Falha na operação: Deve lançar erro se o aluno envolvido na operação for nulo")
    void deveLancarErroSeAlunoForNulo() {
        assertThrows(ValidacaoException.class, () ->
                new Matricula(null, curso, turma, dataMatricula));
    }

    @Test
    @DisplayName("Falha na operação: Deve lançar erro se o curso envolvido na operação for nulo")
    void deveLancarErroSeCursoForNulo() {
        assertThrows(ValidacaoException.class, () ->
                new Matricula(aluno, null, turma, dataMatricula));
    }

    @Test
    @DisplayName("Falha na criação: Deve lançar erro se a data da matrícula for nula")
    void deveLancarErroSeDataMatriculaForNula() {
        assertThrows(ValidacaoException.class, () ->
                new Matricula(aluno, curso, turma, null));
    }

    @Test
    @DisplayName("Sucesso na conclusão: Deve permitir a conclusão de uma matrícula com o registro de uma nota final válida")
    void deveConcluirMatriculaComNotaValida() {
        var matricula = new Matricula(aluno, curso, turma, dataMatricula);
        BigDecimal nota = new BigDecimal("8.0");

        matricula.concluir(nota);

        assertEquals(StatusMatricula.CONCLUIDA, matricula.getStatus());
        assertEquals(nota, matricula.getNotaFinal());
        assertEquals(LocalDate.now(), matricula.getDataConclusao());
    }

    @Test
    @DisplayName("Falha na conclusão: Não deve permitir a conclusão de uma matrícula sem o registro de uma nota final")
    void naoDeveConcluirMatriculaSemNota() {
        var matricula = new Matricula(aluno, curso, turma, dataMatricula);

        var ex = assertThrows(ValidacaoException.class, () -> matricula.concluir(null));
        assertEquals("Nota final é obrigatória para conclusão", ex.getMessage());
    }

    @Test
    @DisplayName("Falha na conclusão: Não deve permitir a conclusão de uma matrícula sem o registro de uma nota final")
    void naoDeveConcluirMatriculaSeNotaForMenorQue7() {
        var matricula = new Matricula(aluno, curso, turma, dataMatricula);

        var ex = assertThrows(ValidacaoException.class, () -> matricula.concluir(new BigDecimal("6.9")));
        assertEquals("Matricula concluida requer nota >= 7", ex.getMessage());
    }

    @Test
    void naoDeveConcluirMatriculaSeStatusNaoForAtiva() {
        var matricula = new Matricula(aluno, curso, turma, dataMatricula);
        matricula.trancar();

        var ex = assertThrows(ValidacaoException.class, () -> matricula.concluir(new BigDecimal("8.0")));
        assertEquals("Apenas matrículas ativas podem ser concluídas", ex.getMessage());
    }

    @Test
    @DisplayName("Falha na conclusão: Não deve permitir a conclusão de uma matrícula cujo status não seja 'Ativa'")
    void deveCancelarMatriculaComMotivo() {
        var matricula = new Matricula(aluno, curso, turma, dataMatricula);
        matricula.cancelar(MotivoCancelamento.DESISTENCIA);

        assertEquals(StatusMatricula.CANCELADA, matricula.getStatus());
        assertEquals(MotivoCancelamento.DESISTENCIA, matricula.getMotivoCancelamento());
    }

    @Test
    @DisplayName("Falha no cancelamento: Não deve permitir o cancelamento de uma matrícula que já foi concluída")
    void naoDeveCancelarMatriculaConcluida() {
        var matricula = new Matricula(aluno, curso, turma, dataMatricula);
        matricula.concluir(new BigDecimal("8.0"));

        var ex = assertThrows(ValidacaoException.class, () ->
                matricula.cancelar(MotivoCancelamento.DESISTENCIA));
        assertEquals("Matricula concluída não pode ser cancelada", ex.getMessage());
    }

    @Test
    @DisplayName("Falha no cancelamento: Não deve permitir o cancelamento de uma matrícula sem um motivo especificado")
    void naoDeveCancelarMatriculaSemMotivo() {
        var matricula = new Matricula(aluno, curso, turma, dataMatricula);

        var ex = assertThrows(ValidacaoException.class, () -> matricula.cancelar(null));
        assertEquals("Motivo do cancelamento é obrigatório", ex.getMessage());
    }

    @Test
    @DisplayName("Sucesso ao trancar: Deve permitir o trancamento de uma matrícula com status 'Ativa'")
    void deveTrancarMatriculaAtiva() {
        var matricula = new Matricula(aluno, curso, turma, dataMatricula);

        matricula.trancar();

        assertEquals(StatusMatricula.TRANCADA, matricula.getStatus());
    }

    @Test
    @DisplayName("Falha ao trancar: Não deve permitir o trancamento de uma matrícula cujo status não seja 'Ativa'")
    void naoDeveTrancarMatriculaNaoAtiva() {
        var matricula = new Matricula(aluno, curso, turma, dataMatricula);
        matricula.trancar();

        var ex = assertThrows(ValidacaoException.class, matricula::trancar);
        assertEquals("Apenas matriculas ativas podem ser trancadas", ex.getMessage());
    }

    @Test
    @DisplayName("Sucesso na reativação: Deve permitir a reativação de uma matrícula que estava 'Trancada'")
    void deveReativarMatriculaTrancada() {
        var matricula = new Matricula(aluno, curso, turma, dataMatricula);

        matricula.trancar();
        matricula.reativar();

        assertEquals(StatusMatricula.ATIVA, matricula.getStatus());
    }

    @Test
    @DisplayName("Falha na reativação: Não deve permitir a reativação de uma matrícula cujo status não seja 'Trancada'")
    void naoDeveReativarMatriculaNaoTrancada() {
        var matricula = new Matricula(aluno, curso, turma, dataMatricula);

        var ex = assertThrows(ValidacaoException.class, matricula::reativar);
        assertEquals("Apenas matrículas trancadas podem ser reativadas", ex.getMessage());
    }

    @Test
    @DisplayName("Falha na conclusão: Deve lançar erro se a data de conclusão for anterior à data da matrícula")
    void deveLancarErroSeDataConclusaoForAntesDaMatricula() {
        var matricula = new Matricula(aluno, curso, turma, dataMatricula);
        matricula.concluir(new BigDecimal("8.0"));

        ReflectionTestUtils.setField(matricula, "dataConclusao", dataMatricula.minusDays(1));

        assertThrows(ValidacaoException.class, () -> {
            matricula.trancar();
        });
    }
}
