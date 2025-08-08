package com.edutech.api.domain.matricula.service;

import com.edutech.api.domain.aluno.Aluno;
import com.edutech.api.domain.aluno.repository.AlunoRepository;
import com.edutech.api.domain.curso.Curso;
import com.edutech.api.domain.curso.enums.CategoriaCurso;
import com.edutech.api.domain.curso.enums.NivelCurso;
import com.edutech.api.domain.curso.repository.CursoRepository;
import com.edutech.api.domain.endereco.Endereco;
import com.edutech.api.domain.enums.Modalidade;
import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.matricula.Matricula;
import com.edutech.api.domain.matricula.dto.MatriculaCreateDTO;
import com.edutech.api.domain.matricula.dto.MatriculaDetalhesDTO;
import com.edutech.api.domain.matricula.dto.MatriculaResumoDTO;
import com.edutech.api.domain.matricula.enums.MotivoCancelamento;
import com.edutech.api.domain.matricula.enums.StatusMatricula;
import com.edutech.api.domain.matricula.mapper.MatriculaMapper;
import com.edutech.api.domain.matricula.repository.MatriculaRepository;
import com.edutech.api.domain.matricula.validadores.ValidadorCadastroMatricula;
import com.edutech.api.domain.turma.Turma;
import com.edutech.api.domain.turma.repository.TurmaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Matricula Service Testes")
class MatriculaServiceTest {

    @InjectMocks
    private MatriculaService service;
    @Mock
    private MatriculaRepository matriculaRepository;
    @Mock
    private MatriculaMapper matriculaMapper;
    @Mock
    private AlunoRepository alunoRepository;
    @Mock
    private TurmaRepository turmaRepository;
    @Mock
    private CursoRepository cursoRepository;
    @Mock
    private List<ValidadorCadastroMatricula> validadores;

    private Endereco endereco;
    private Aluno aluno;
    private Turma turma;
    private Matricula matricula;
    private MatriculaResumoDTO resumoDTO;

    @BeforeEach
    void setup(){
        endereco = new Endereco(
                "Avenida Brasil", "Jardim América", "54321-000", "2000",
                "Bloco B", "Rio de Janeiro", "RJ"
        );

        aluno = new Aluno(
                "Maria Oliveira", "maria.oliveira@email.com", "(21) 91234-5678",
                "987.654.321-00", LocalDate.of(1985, 10, 22),
                endereco
        );

        turma = new Turma(
                "TURMA-2024-03", LocalDate.of(2024, 3, 1), LocalDate.of(2024, 12, 15),
                LocalTime.of(8, 0), LocalTime.of(9, 30), 15,
                Modalidade.EAD
        );

        resumoDTO = new MatriculaResumoDTO(
                1L, LocalDate.of(2025,04,20), 1L, "Maria Oliveira",
                 3L, "TURMA-2024-03", StatusMatricula.ATIVA
        );

        matricula = new Matricula(aluno, turma, LocalDate.of(2025,5,20));
    }

    @Test
    @DisplayName("Sucesso no cadastro: Deve permitir o registro de uma nova matrícula com todos os dados válidos")
    void deveCadastrarMatriculaComSucesso() {
        var curso = new Curso(
                "Desenvolvimento Web Full Stack",
                "Curso completo de desenvolvimento web com React e Spring Boot",
                240, 6, NivelCurso.INTERMEDIARIO, CategoriaCurso.PROGRAMACAO
        );

        turma.vincularCurso(curso);

        var matriculaCreateDTO = new MatriculaCreateDTO(
                1L, 3L, LocalDate.of(2025, 4, 20)
        );

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(turmaRepository.findById(3L)).thenReturn(Optional.of(turma));
        when(matriculaRepository.save(any(Matricula.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(matriculaMapper.toResumoDTO(any(Matricula.class))).thenReturn(resumoDTO);

        var result = service.cadastrarMatricula(matriculaCreateDTO);

        ArgumentCaptor<Matricula> matriculaCaptor = ArgumentCaptor.forClass(Matricula.class);
        verify(matriculaRepository).save(matriculaCaptor.capture());
        var matriculaCapturada = matriculaCaptor.getValue();

        assertAll(
                // Validação do DTO de retorno
                () -> assertNotNull(result),
                () -> assertEquals("Maria Oliveira", result.nomeAluno()),
                () -> assertEquals(1L, result.alunoId()),
                () -> assertEquals(3L, result.turmaId()),
                () -> assertEquals(StatusMatricula.ATIVA, result.status()),

                // Validação do objeto capturado
                () -> assertNotNull(matriculaCapturada),
                () -> assertEquals(aluno, matriculaCapturada.getAluno()),
                () -> assertEquals(turma, matriculaCapturada.getTurma()),
                () -> assertEquals(matriculaCreateDTO.dataMatricula(), matriculaCapturada.getDataMatricula()),
                () -> assertEquals(StatusMatricula.ATIVA, matriculaCapturada.getStatus()),

                // Validação específica do curso (que vem através da turma)
                () -> assertNotNull(matriculaCapturada.getTurma().getCurso()),
                () -> assertEquals(curso, matriculaCapturada.getTurma().getCurso())
        );

        verify(alunoRepository).findById(1L);
        verify(turmaRepository).findById(3L);
        verify(matriculaRepository).save(any(Matricula.class));
        verify(matriculaMapper).toResumoDTO(matriculaCapturada);
    }

    @Test
    @DisplayName("Sucesso na busca por ID: Deve retornar os detalhes completos de uma matrícula quando o ID fornecido for válido")
    void deveDetalharMatriculaPorId() {
        var detalhesDTO = new MatriculaDetalhesDTO(
                1L, LocalDate.of(2025, 4, 20), LocalDate.of(2025, 10, 20),
                null, StatusMatricula.ATIVA, null, 1L,"Maria Oliveira",
                3L,"TURMA-2024-03"
        );

        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));
        when(matriculaMapper.toDetalhesDTO(matricula)).thenReturn(detalhesDTO);

        var result = service.detalharPorId(1L);

        assertAll(
                () -> {
                    assertNotNull(result);
                    assertEquals(StatusMatricula.ATIVA, detalhesDTO.status());
                    assertNull(detalhesDTO.motivoCancelamento());
                },
                () -> {
                    assertEquals(LocalDate.of(2025, 4, 20), detalhesDTO.dataMatricula());
                    assertEquals(LocalDate.of(2025, 10, 20), detalhesDTO.dataConclusao());
                    assertNull(detalhesDTO.notaFinal());
                },
                () -> {
                    assertEquals("Maria Oliveira", detalhesDTO.nomeAluno());
                }
        );
        verify(matriculaRepository).findById(1L);
        verify(matriculaMapper).toDetalhesDTO(matricula);
    }

    @Test
    @DisplayName("Sucesso na busca por nome do aluno: Deve retornar as matrículas associadas a um aluno específico pelo nome")
    void deveBuscarMatriculasPorNomeDoAluno() {
        when(matriculaRepository.findByAlunoNome("Maria Oliveira")).thenReturn(List.of(matricula));
        when(matriculaMapper.toResumoDTO(matricula)).thenReturn(resumoDTO);

        var result = service.buscarPorNomeDoAluno("Maria Oliveira");

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1, result.size())
        );
        verify(matriculaRepository).findByAlunoNome("Maria Oliveira");
        verify(matriculaMapper).toResumoDTO(matricula);
    }

    @Test
    @DisplayName("Falha na validação: Deve lançar exceção ao tentar operar com um nome vazio")
    void deveLancarExcecaoSeNomeForVazio() {
        var ex = assertThrows(ValidacaoException.class, () -> service.buscarPorNomeDoAluno("  "));

        assertAll(() -> assertEquals("Nome do aluno é obrigatório.", ex.getMessage()));
    }

    @Test
    @DisplayName("Falha na busca: Deve lançar exceção quando nenhuma matrícula for encontrada com os critérios fornecidos")
    void deveLancarExcecaoSeNaoEncontrarMatriculas() {
        when(matriculaRepository.findByAlunoNome("Lucas")).thenReturn(List.of());

        var ex = assertThrows(ValidacaoException.class, () -> service.buscarPorNomeDoAluno("Lucas"));

        assertAll(() -> assertTrue(ex.getMessage().contains("Aluno não possui matricula cadastrada")));
    }

    @Test
    @DisplayName("Sucesso na busca paginada: Deve retornar todas as matrículas, aplicando a paginação corretamente")
    void deveBuscarTodasMatriculasPaginadas() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Matricula> page = new PageImpl<>(List.of(matricula));

        when(matriculaRepository.findAll(pageable)).thenReturn(page);
        when(matriculaMapper.toResumoDTO(matricula)).thenReturn(resumoDTO);

        Page<MatriculaResumoDTO> result = service.buscarTodasMatriculas(pageable);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(1, result.getTotalElements())
        );
        verify(matriculaRepository).findAll(pageable);
        verify(matriculaMapper).toResumoDTO(matricula);
    }

    @Test
    @DisplayName("Sucesso na conclusão: Deve permitir a conclusão de uma matrícula com o registro de uma nota final válida")
    void deveConcluirMatriculaComNotaValida() {
        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));
        when(matriculaMapper.toResumoDTO(matricula)).thenReturn(resumoDTO);

        var result = service.concluirMatricula(1L, new BigDecimal("9.0"));

        assertNotNull(result);

        verify(matriculaRepository).findById(1L);
        verify(matriculaRepository).save(matricula);
        verify(matriculaMapper).toResumoDTO(matricula);
    }

    @Test
    @DisplayName("Sucesso no trancamento: Deve permitir o trancamento de uma matrícula de forma bem-sucedida")
    void deveTrancarMatriculaComSucesso() {
        when(matriculaRepository.findById(1L)).thenReturn(Optional.of(matricula));
        when(matriculaMapper.toResumoDTO(matricula)).thenReturn(resumoDTO);

        var result = service.trancarMatricula(1L);

        assertAll(
                () -> assertNotNull(result),
                () -> assertEquals(StatusMatricula.TRANCADA, matricula.getStatus())
        );
        verify(matriculaRepository).findById(1L);
        verify(matriculaRepository).save(matricula);
        verify(matriculaMapper).toResumoDTO(matricula);
    }

    @Test
    @DisplayName("Sucesso na reativação: Deve permitir a reativação de uma matrícula que estava trancada")
    void deveReativarMatriculaComSucesso() {
        Long matriculaId = 1L;

        matricula.trancar();

        when(matriculaRepository.findById(matriculaId)).thenReturn(Optional.of(matricula));
        when(matriculaRepository.save(matricula)).thenReturn(matricula);
        when(matriculaMapper.toResumoDTO(matricula)).thenReturn(resumoDTO);

        MatriculaResumoDTO resultado = service.reativarMatricula(matriculaId);

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(resumoDTO, resultado),
                () -> assertEquals(StatusMatricula.ATIVA, matricula.getStatus())
        );
        verify(matriculaRepository).save(matricula);
        verify(matriculaMapper).toResumoDTO(matricula);
    }

    @Test
    @DisplayName("Sucesso no cancelamento: Deve permitir o cancelamento de uma matrícula de forma bem-sucedida")
    void deveCancelarMatriculaComSucesso() {
        Long matriculaId = 2L;
        MotivoCancelamento motivo = MotivoCancelamento.DESISTENCIA;

        when(matriculaRepository.findById(matriculaId)).thenReturn(Optional.of(matricula));
        when(matriculaRepository.save(matricula)).thenReturn(matricula);
        when(matriculaMapper.toResumoDTO(matricula)).thenReturn(resumoDTO);

        MatriculaResumoDTO resultado = service.cancelarMatricula(matriculaId, motivo);

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(resumoDTO, resultado),
                () -> assertEquals(StatusMatricula.CANCELADA, matricula.getStatus()),
                () -> assertEquals(motivo, matricula.getMotivoCancelamento())
        );
        verify(matriculaRepository).save(matricula);
        verify(matriculaMapper).toResumoDTO(matricula);
    }
}
