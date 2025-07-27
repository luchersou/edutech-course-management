package com.edutech.api.domain.turma;

import com.edutech.api.domain.curso.Curso;
import com.edutech.api.domain.enums.Modalidade;
import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.matricula.Matricula;
import com.edutech.api.domain.professor.Professor;
import com.edutech.api.domain.turma.enums.StatusTurma;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes unitários da entidade Turma")
class TurmaTest {

    private Turma turma;

    @BeforeEach
    void setup() {
        turma = new Turma(
                "TURMA001",
                LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(30),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0),
                20,
                Modalidade.EAD
        );
    }

    @Test
    @DisplayName("Sucesso ao criar: Deve permitir o cadastro de uma nova turma com dados válidos e completos")
    void deveCriarTurmaComDadosValidos() {
        assertAll(
                () -> assertEquals("TURMA001", turma.getCodigo()),
                () -> assertEquals(StatusTurma.ABERTA, turma.getStatus()),
                () -> assertEquals(Modalidade.EAD, turma.getModalidade()),
                () -> assertEquals(20, turma.getVagasTotais())
        );
    }

    @Test
    @DisplayName("Falha na criação/atualização: Deve lançar exceção se a data de término for anterior à data de início da turma")
    void deveLancarExcecaoSeDataFimForAntesDaDataInicio() {
        assertThrows(ValidacaoException.class, () -> new Turma(
                "TURMA002",
                LocalDate.now().plusDays(10),
                LocalDate.now().plusDays(5),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0),
                10,
                Modalidade.HIBRIDO
        ));
    }

    @Test
    @DisplayName("Falha no horário: Deve lançar exceção se o horário de término for anterior ou igual ao horário de início da turma")
    void deveLancarExcecaoSeHorarioFimForAntesOuIgualAoHorarioInicio() {
        assertThrows(ValidacaoException.class, () -> new Turma(
                "TURMA003",
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                LocalTime.of(14, 0),
                LocalTime.of(13, 0),
                15,
                Modalidade.PRESENCIAL
        ));
    }

    @Test
    @DisplayName("Falha na criação/atualização: Deve lançar exceção se o número de vagas for zero ou negativo")
    void deveLancarExcecaoSeVagasForemZeroOuNegativas() {
        assertThrows(ValidacaoException.class, () -> new Turma(
                "TURMA004",
                LocalDate.now(),
                LocalDate.now().plusDays(30),
                LocalTime.of(14, 0),
                LocalTime.of(15, 0),
                0,
                Modalidade.EAD
        ));
    }

    @Test
    @DisplayName("Sucesso ao atualizar: Deve permitir a modificação dos dados de uma turma existente com sucesso")
    void deveAtualizarTurmaComSucesso() {
        turma.atualizar(
                "TURMA_ATUALIZADA",
                LocalDate.now().minusDays(10),
                LocalDate.now().plusDays(40),
                LocalTime.of(10, 0),
                LocalTime.of(13, 0),
                25,
                Modalidade.PRESENCIAL
        );

        assertAll(
                () -> assertEquals("TURMA_ATUALIZADA", turma.getCodigo()),
                () -> assertEquals(25, turma.getVagasTotais()),
                () -> assertEquals(LocalTime.of(10, 0), turma.getHorarioInicio()),
                () -> assertEquals(Modalidade.PRESENCIAL, turma.getModalidade())
        );
    }

    @Test
    @DisplayName("Sucesso ao iniciar: Deve mudar o status da turma para 'EM_ANDAMENTO' se estiver 'ABERTA' e a data de início já tiver sido atingida")
    void deveIniciarTurmaSeStatusForAbertaEDataInicioPassada() {
        turma.iniciar();
        assertEquals(StatusTurma.EM_ANDAMENTO, turma.getStatus());
    }

    @Test
    @DisplayName("Falha na inicialização: Não deve iniciar a turma se o status atual não for 'Aberta'")
    void naoDeveIniciarTurmaComStatusDiferenteDeAberta() {
        turma.iniciar();
        assertThrows(ValidacaoException.class, turma::iniciar);
    }

    @Test
    @DisplayName("Falha na inicialização: Não deve permitir que a turma seja iniciada antes da sua data de início programada")
    void naoDeveIniciarTurmaAntesDaDataInicio() {
        var futura = new Turma(
                "FUTURA",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5),
                LocalTime.of(8, 0),
                LocalTime.of(11, 0),
                15,
                Modalidade.EAD
        );
        assertThrows(ValidacaoException.class, futura::iniciar);
    }

    @Test
    @DisplayName("Sucesso ao concluir: Deve finalizar a turma se estiver 'EM_ANDAMENTO' e a data de término já tiver sido alcançada")
    void deveConcluirTurmaSeEstiverEmAndamentoEDataFimPassada() {
        turma.iniciar();
        turma.atualizar(null, null, LocalDate.now().minusDays(1), null, null, null, null);
        turma.concluir();
        assertEquals(StatusTurma.CONCLUIDA, turma.getStatus());
    }

    @Test
    @DisplayName("Falha na conclusão: Não deve permitir a conclusão de uma turma cujo status não seja 'Em Andamento'")
    void naoDeveConcluirTurmaSeNaoEstiverEmAndamento() {
        assertThrows(ValidacaoException.class, turma::concluir);
    }

    @Test
    @DisplayName("Falha na conclusão: Não deve permitir que a turma seja concluída antes da sua data de término programada")
    void naoDeveConcluirTurmaAntesDaDataFim() {
        turma.iniciar();
        turma.atualizar(null, null, LocalDate.now().plusDays(10), null, null, null, null);
        assertThrows(ValidacaoException.class, turma::concluir);
    }

    @Test
    @DisplayName("Sucesso ao cancelar: Deve permitir o cancelamento de turmas com status 'Aberta' ou 'Em Andamento'")
    void deveCancelarTurmaAbertaOuEmAndamento() {
        turma.cancelar();
        assertEquals(StatusTurma.CANCELADA, turma.getStatus());
    }

    @Test
    @DisplayName("Falha no cancelamento: Não deve permitir o cancelamento de uma turma que já esteja 'Cancelada' ou 'Concluída'")
    void naoDeveCancelarTurmaJaCanceladaOuConcluida() {
        turma.cancelar();
        assertThrows(ValidacaoException.class, turma::cancelar);

        turma = new Turma(
                "TURMA_CONCLUIDA",
                LocalDate.now().minusDays(10),
                LocalDate.now().minusDays(1),
                LocalTime.of(8, 0),
                LocalTime.of(11, 0),
                10,
                Modalidade.HIBRIDO
        );
        turma.iniciar();
        turma.concluir();
        assertThrows(ValidacaoException.class, turma::cancelar);
    }

    @Test
    @DisplayName("Sucesso no cálculo: Deve calcular o número de vagas disponíveis na turma de forma correta")
    void deveCalcularVagasDisponiveisCorretamente() {
        var m1 = mock(Matricula.class);
        var m2 = mock(Matricula.class);
        turma.getMatriculas().addAll(List.of(m1, m2));

        assertEquals(18, turma.getVagasDisponiveis());
    }

    @Test
    @DisplayName("Cenário completo: Deve vincular e desvincular um professor de uma turma com sucesso")
    void deveVincularEDesvincularProfessorComSucesso() {
        var professor = mock(Professor.class);
        turma.vincularProfessor(professor);
        assertEquals(professor, turma.getProfessor());

        turma.desvincularProfessor();
        assertNull(turma.getProfessor());
    }

    @Test
    @DisplayName("Cenário completo: Deve vincular e desvincular um curso de uma turma com sucesso")
    void deveVincularEDesvincularCursoComSucesso() {
        var curso = mock(Curso.class);
        turma.vincularCurso(curso);
        assertEquals(curso, turma.getCurso());

        turma.desvincularCurso();
        assertNull(turma.getCurso());
    }
}

