package com.edutech.api.domain.turma.service;

import com.edutech.api.domain.curso.Curso;
import com.edutech.api.domain.curso.enums.CategoriaCurso;
import com.edutech.api.domain.curso.enums.NivelCurso;
import com.edutech.api.domain.curso.repository.CursoRepository;
import com.edutech.api.domain.endereco.Endereco;
import com.edutech.api.domain.enums.Modalidade;
import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.professor.Professor;
import com.edutech.api.domain.professor.repository.ProfessorRepository;
import com.edutech.api.domain.turma.Turma;
import com.edutech.api.domain.turma.dto.TurmaCreateDTO;
import com.edutech.api.domain.turma.dto.TurmaDetalhesDTO;
import com.edutech.api.domain.turma.dto.TurmaResumoDTO;
import com.edutech.api.domain.turma.dto.TurmaUpdateDTO;
import com.edutech.api.domain.turma.enums.StatusTurma;
import com.edutech.api.domain.turma.mapper.TurmaMapper;
import com.edutech.api.domain.turma.repository.TurmaRepository;
import com.edutech.api.domain.turma.validacoes.atualiza_turma.ValidadorAtualizaTurma;
import com.edutech.api.domain.turma.validacoes.cadastra_turma.ValidadorCadastroTurma;
import com.edutech.api.domain.turma.validacoes.desvincula_curso.ValidadorDesvinculoCurso;
import com.edutech.api.domain.turma.validacoes.desvincula_professor.ValidadorDesvinculoProfessor;
import com.edutech.api.domain.turma.validacoes.inicia_turma.ValidadorIniciarTurma;
import com.edutech.api.domain.turma.validacoes.vincula_curso.ValidadorVinculoCurso;
import com.edutech.api.domain.turma.validacoes.vincula_professor.ValidadorVinculoProfessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Turma Service Testes")
class TurmaServiceTest {

    @InjectMocks
    private TurmaService turmaService;
    @Mock
    private TurmaRepository turmaRepository;
    @Mock
    private ProfessorRepository professorRepository;
    @Mock
    private CursoRepository cursoRepository;
    @Mock
    private TurmaMapper turmaMapper;
    @Mock
    private List<ValidadorCadastroTurma> validadoresCadastroTurma;
    @Mock
    private List<ValidadorAtualizaTurma> validadoresAtualizaTurma;
    @Mock
    private List<ValidadorVinculoProfessor> validadoresVinculoProfessor;
    @Mock
    private List<ValidadorDesvinculoProfessor> validadoresDesvinculoProfessor;
    @Mock
    private List<ValidadorVinculoCurso> validadoresVinculoCurso;
    @Mock
    private List<ValidadorDesvinculoCurso> validadoresDesvinculoCurso;
    @Mock
    private List<ValidadorIniciarTurma> validadorIniciaTurmas;

    @BeforeEach
    void setup(){
        turmaService = new TurmaService(
                turmaRepository,
                professorRepository,
                cursoRepository,
                turmaMapper,
                validadoresCadastroTurma,
                validadoresAtualizaTurma,
                validadoresVinculoProfessor,
                validadoresDesvinculoProfessor,
                validadoresVinculoCurso,
                validadoresDesvinculoCurso,
                validadorIniciaTurmas
        );
    }

    @Test
    @DisplayName("Sucesso ao cadastrar: Deve criar uma nova turma com dados válidos")
    void deveCadastrarTurmaComDadosCorretos() {
        var turmaCreateDTO = new TurmaCreateDTO(
                "TURMA-2024-02", LocalDate.of(2025,5,20),
                LocalDate.of(2025, 12, 15),
                LocalTime.of(19, 0), LocalTime.of(20, 30),
                15, Modalidade.PRESENCIAL
        );

        var turmaResumoDTO = new TurmaResumoDTO(
                1L, "TURMA-2024-02", LocalDate.of(2025,5,20),
                LocalDate.of(2025, 12, 15), StatusTurma.ABERTA
        );

        when(turmaMapper.toResumoDTO(any(Turma.class))).thenReturn(turmaResumoDTO);
        when(turmaRepository.save(any(Turma.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var resultado = turmaService.cadastrarTurma(turmaCreateDTO);

        ArgumentCaptor<Turma> captor = ArgumentCaptor.forClass(Turma.class);
        verify(turmaRepository).save(captor.capture());

        Turma turmaSalva = captor.getValue();

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals("TURMA-2024-02", turmaSalva.getCodigo()),
                () -> assertEquals(LocalDate.of(2025,5,20), turmaSalva.getDataInicio()),
                () -> assertEquals(StatusTurma.ABERTA, turmaSalva.getStatus())
        );
        verify(turmaMapper).toResumoDTO(captor.getValue());
    }

    @Test
    @DisplayName("Sucesso ao atualizar: Deve modificar os dados de uma turma existente com sucesso")
    void deveAtualizarTurmaComSucesso() {
        var turma = new Turma(
                "TURMA-2024-02", LocalDate.of(2025,5,20),
                LocalDate.of(2025, 12, 15), LocalTime.of(19, 0),
                LocalTime.of(20, 30), 20, Modalidade.EAD
        );

        var dto = new TurmaUpdateDTO(
                "TURMA-NEW", LocalDate.of(2024, 2, 1),
                LocalDate.of(2024, 12, 15),
                LocalTime.of(8, 0), LocalTime.of(9, 30),
                15,Modalidade.EAD
        );

        var turmaResumoDTO = new TurmaResumoDTO(
                1L, "TURMA-NEW", LocalDate.of(2024, 2, 1),
                LocalDate.of(2024, 12, 15), StatusTurma.ABERTA
        );

        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
        when(turmaRepository.save(any(Turma.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(turmaMapper.toResumoDTO(any())).thenReturn(turmaResumoDTO);

        var resultado = turmaService.atualizarTurma(1L, dto);

        ArgumentCaptor<Turma> captor = ArgumentCaptor.forClass(Turma.class);
        verify(turmaRepository).save(captor.capture());

        var turmaAtualizada = captor.getValue();

        assertAll(
                () -> assertEquals("TURMA-NEW", turmaAtualizada.getCodigo()),
                () -> assertEquals(LocalDate.of(2024, 2, 1), turmaAtualizada.getDataInicio()),
                () -> assertEquals(Modalidade.EAD, turmaAtualizada.getModalidade()),
                () -> assertEquals(turmaResumoDTO, resultado)
        );
        verify(turmaRepository).findById(1L);
    }

    @Test
    @DisplayName("Sucesso ao detalhar: Deve exibir os detalhes de uma turma com sucesso")
    void deveDetalharTurmaPorIdComSucesso() {
        var turma = new Turma(
                "TURMA-2024-02", LocalDate.of(2025,5,20),
                LocalDate.of(2025, 12, 15), LocalTime.of(19, 0),
                LocalTime.of(20, 30), 20, Modalidade.EAD
        );

        var turmaDetalhesDTO = new TurmaDetalhesDTO(
                1L, "TURMA-2024-02", LocalDate.of(2025, 3, 1),
                LocalDate.of(2025, 12, 15),
                LocalTime.of(19, 0), LocalTime.of(20, 30),
                Modalidade.PRESENCIAL, 20, 5, StatusTurma.ABERTA
        );

        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
        when(turmaMapper.toDetalhesDTO(turma)).thenReturn(turmaDetalhesDTO);

        var resultado = turmaService.detalharPorId(1L);

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(turmaDetalhesDTO, resultado)
        );
        verify(turmaMapper).toDetalhesDTO(turma);
    }

    @Test
    @DisplayName("Falha na operação: Deve lançar exceção quando a turma alvo não for encontrada")
    void deveLancarExcecaoQuandoTurmaNaoForEncontrada() {
        when(turmaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ValidacaoException.class, () ->
                turmaService.detalharPorId(99L)
        );
    }

    @Test
    @DisplayName("Sucesso ao buscar: Deve retornar todas as turmas cadastradas")
    void deveBuscarTodasTurmasComSucesso() {
        var turma = new Turma(
                "TURMA-2024-02", LocalDate.of(2025,5,20),
                LocalDate.of(2025, 12, 15), LocalTime.of(19, 0),
                LocalTime.of(20, 30), 20, Modalidade.EAD
        );

        var turmaResumoDTO = new TurmaResumoDTO(
                1L, "TURMA-2024-02", LocalDate.of(2025,5,20),
                LocalDate.of(2025, 12, 15), StatusTurma.ABERTA
        );

        var pageable = PageRequest.of(0, 10);
        var page = new PageImpl<>(List.of(turma));

        when(turmaRepository.findAll(pageable)).thenReturn(page);
        when(turmaMapper.toResumoDTO(turma)).thenReturn(turmaResumoDTO);

        var resultado = turmaService.buscarTodasTurmas(pageable);

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(1, resultado.getTotalElements()),
                () -> assertEquals(turmaResumoDTO, resultado.getContent().getFirst())
        );
    }

    @Test
    @DisplayName("Sucesso ao iniciar: Deve mudar o status de uma turma para 'EM_ANDAMENTO' com sucesso")
    void deveIniciarTurmaComSucesso() {
        var turma = new Turma(
                "TURMA-2024-02", LocalDate.of(2025,5,20),
                LocalDate.of(2025, 12, 15), LocalTime.of(19, 0),
                LocalTime.of(20, 30), 20, Modalidade.EAD
        );

        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));

        turmaService.iniciarTurma(1L);

        assertEquals(StatusTurma.EM_ANDAMENTO, turma.getStatus());
        verify(turmaRepository).save(turma);
    }

    @Test
    @DisplayName("Falha na atualização: Deve lançar exceção ao tentar atualizar uma turma que não está cadastrada")
    void deveLancarExcecaoAoAtualizarTurmaInexistente() {
        var dto = new TurmaUpdateDTO(
                "TURMA-2026-01",
                LocalDate.of(2026, 1, 10),
                LocalDate.of(2026, 6, 30),
                LocalTime.of(9, 0),
                LocalTime.of(11, 0),
                20,
                Modalidade.EAD
        );

        when(turmaRepository.findById(999L)).thenReturn(Optional.empty());

        var exception = assertThrows(ValidacaoException.class, () ->
                turmaService.atualizarTurma(999L, dto)
        );

        assertEquals("Turma com ID 999 não encontrada", exception.getMessage());
    }

    @Test
    @DisplayName("Sucesso ao concluir: Deve finalizar uma turma que já atingiu a data prevista de término")
    void deveConcluirTurmaComSucesso() {
        var turma = new Turma(
                "TURMA-2024-02", LocalDate.of(2025,5,20),
                LocalDate.of(2025, 12, 15), LocalTime.of(19, 0),
                LocalTime.of(20, 30), 20, Modalidade.EAD
        );

        turma.iniciar();
        turma.atualizar(null, null, LocalDate.now().minusDays(1), null, null, null, null);

        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));

        turmaService.concluirTurma(1L);

        assertEquals(StatusTurma.CONCLUIDA, turma.getStatus());
        verify(turmaRepository).save(turma);
    }

    @Test
    @DisplayName("Falha na conclusão: Deve lançar exceção ao tentar finalizar uma turma antes da sua data de término")
    void deveLancarExcecaoAoConcluirTurmaAntesDaDataFim() {
        var turma = new Turma(
                "TURMA-2024-02", LocalDate.of(2025,5,20),
                LocalDate.of(2025, 12, 15), LocalTime.of(19, 0),
                LocalTime.of(20, 30), 20, Modalidade.EAD
        );

        turma.iniciar();
        turma.atualizar(null, null, LocalDate.now().plusDays(2), null, null, null, null);

        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));

        assertThrows(ValidacaoException.class, () -> turmaService.concluirTurma(1L));
    }

    @Test
    @DisplayName("Sucesso ao cancelar: Deve alterar o status de uma turma ativa para 'cancelada' com sucesso")
    void deveCancelarTurmaComSucesso() {
        var turma = new Turma(
                "TURMA-2024-02", LocalDate.of(2025,5,20),
                LocalDate.of(2025, 12, 15), LocalTime.of(19, 0),
                LocalTime.of(20, 30), 20, Modalidade.EAD
        );

        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));

        turmaService.cancelarTurma(1L);

        assertEquals(StatusTurma.CANCELADA, turma.getStatus());
    }

    @Test
    @DisplayName("Falha no cancelamento: Deve lançar exceção ao tentar cancelar uma turma que já está com o status 'concluída'")
    void deveLancarExcecaoAoCancelarTurmaConcluida() {
        var turma = new Turma(
                "TURMA-2024-02", LocalDate.of(2025,1,20),
                LocalDate.of(2025, 5, 15), LocalTime.of(19, 0),
                LocalTime.of(20, 30), 20, Modalidade.EAD
        );
        turma.iniciar();
        turma.concluir();

        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));

        assertThrows(ValidacaoException.class, () -> turmaService.cancelarTurma(1L));
    }

    /**
     * Vincular/Desvincular professor de turma
     */
    @Test
    @DisplayName("Sucesso ao vincular: Deve associar um professor a uma turma específica com sucesso")
    void deveVincularProfessorComSucesso() {
        var endereco = new Endereco(
                "Rua das Flores", "Jardim Paulista", "01415001",
                "100", "Sala 302", "São Paulo", "SP"
        );

        var turma = new Turma(
                "TURMA-2024-02", LocalDate.of(2025,5,20),
                LocalDate.of(2025, 12, 15), LocalTime.of(19, 0),
                LocalTime.of(20, 30), 20, Modalidade.EAD
        );

        var professor = new Professor(
                "Carlos Silva", "carlos.silva@academia.com", LocalDate.of(1985, 5, 15),
                "(11)98765-4321", "12345678901", Modalidade.EAD, endereco
        );

        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
        when(professorRepository.findById(2L)).thenReturn(Optional.of(professor));

        turmaService.vincularProfessor(1L, 2L);

        assertEquals(professor, turma.getProfessor());
        verify(turmaRepository).save(turma);
    }

    @Test
    @DisplayName("Falha ao vincular professor: Deve lançar exceção se o professor especificado não for encontrado")
    void deveLancarExcecaoSeProfessorNaoForEncontradoAoVincular() {
        var turma = new Turma(
                "TURMA-2024-02", LocalDate.of(2025,5,20),
                LocalDate.of(2025, 12, 15), LocalTime.of(19, 0),
                LocalTime.of(20, 30), 20, Modalidade.EAD
        );

        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
        when(professorRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ValidacaoException.class, () -> turmaService.vincularProfessor(1L, 2L));
    }

    @Test
    @DisplayName("Sucesso ao desvincular: Deve remover a associação de um professor a uma turma com sucesso")
    void deveDesvincularProfessorComSucesso() {
        var endereco = new Endereco(
                "Rua das Flores", "Jardim Paulista", "01415001",
                "100", "Sala 302", "São Paulo", "SP"
        );

        var turma = new Turma(
                "TURMA-2024-02", LocalDate.of(2025,5,20),
                LocalDate.of(2025, 12, 15), LocalTime.of(19, 0),
                LocalTime.of(20, 30), 20, Modalidade.EAD
        );

        var professor = new Professor(
                "Carlos Silva", "carlos.silva@academia.com", LocalDate.of(1985, 5, 15),
                "(11)98765-4321", "12345678901", Modalidade.EAD, endereco
        );

        turma.vincularProfessor(professor);

        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
        when(professorRepository.findById(2L)).thenReturn(Optional.of(professor));

        turmaService.desvincularProfessor(1L, 2L);

        assertNull(turma.getProfessor());
        verify(turmaRepository).save(turma);
    }

    @Test
    @DisplayName("Falha ao desvincular: Deve lançar exceção se a turma não for encontrada ao tentar desvincular")
    void deveLancarExcecaoSeTurmaNaoForEncontradaAoDesvincular() {
        when(turmaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ValidacaoException.class, () -> turmaService.desvincularProfessor(1L, 2L));
    }

    /**
     * Vincular/Desvincular curso de turma
     */
    @Test
    @DisplayName("Sucesso ao vincular: Deve associar um curso a uma turma específica com sucesso")
    void deveVincularCursoComSucesso() {
        Long turmaId = 1L;
        Long cursoId = 2L;

        var turma = new Turma(
                "TURMA-2024-02", LocalDate.of(2025,5,20),
                LocalDate.of(2025, 12, 15), LocalTime.of(19, 0),
                LocalTime.of(20, 30), 20, Modalidade.EAD
        );

        var curso = new Curso(
                "Desenvolvimento Web Full Stack",
                "Curso completo de desenvolvimento web com React e Spring Boot",
                240, 6, NivelCurso.INTERMEDIARIO, CategoriaCurso.PROGRAMACAO
        );

        when(turmaRepository.findById(turmaId)).thenReturn(Optional.of(turma));
        when(cursoRepository.findById(cursoId)).thenReturn(Optional.of(curso));

        turmaService.vincularCurso(turmaId, cursoId);

        ArgumentCaptor<Turma> captor = ArgumentCaptor.forClass(Turma.class);
        verify(turmaRepository).save(captor.capture());

        Turma turmaSalva = captor.getValue();
        assertEquals(curso, turmaSalva.getCurso());
        assertEquals(turma, turmaSalva);

        verify(turmaRepository).findById(turmaId);
        verify(cursoRepository).findById(cursoId);
    }

    @Test
    @DisplayName("Falha ao vincular curso: Deve lançar exceção se o curso especificado não for encontrado")
    void deveLancarExcecaoSeCursoNaoForEncontradoAoVincular() {
        var turma = new Turma(
                "TURMA-2024-02", LocalDate.of(2025,5,20),
                LocalDate.of(2025, 12, 15), LocalTime.of(19, 0),
                LocalTime.of(20, 30), 20, Modalidade.EAD
        );

        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
        when(cursoRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ValidacaoException.class, () -> turmaService.vincularCurso(1L, 2L));
    }

    @Test
    @DisplayName("Sucesso ao desvincular: Deve remover a associação de um curso a uma turma com sucesso")
    void deveDesvincularCursoComSucesso() {
        var turma = new Turma(
                "TURMA-2024-02", LocalDate.of(2025,5,20),
                LocalDate.of(2025, 12, 15), LocalTime.of(19, 0),
                LocalTime.of(20, 30), 20, Modalidade.EAD
        );

        var curso = new Curso(
                "Desenvolvimento Web Full Stack",
                "Curso completo de desenvolvimento web com React e Spring Boot",
                240, 6, NivelCurso.INTERMEDIARIO, CategoriaCurso.PROGRAMACAO
        );

        turma.vincularCurso(curso);

        when(turmaRepository.findById(1L)).thenReturn(Optional.of(turma));
        when(cursoRepository.findById(2L)).thenReturn(Optional.of(curso));

        turmaService.desvincularCurso(1L, 2L);

        assertNull(turma.getCurso());
        verify(turmaRepository).save(turma);
    }

    @Test
    @DisplayName("Falha ao desvincular curso: Deve lançar exceção se a turma não for encontrada durante a desvinculação do curso")
    void deveLancarExcecaoSeTurmaNaoForEncontradaAoDesvincularCurso() {
        when(turmaRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ValidacaoException.class, () -> turmaService.desvincularCurso(1L, 2L));
    }
}
