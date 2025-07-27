package com.edutech.api.controller;

import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.matricula.dto.*;
import com.edutech.api.domain.matricula.enums.MotivoCancelamento;
import com.edutech.api.domain.matricula.enums.StatusMatricula;
import com.edutech.api.domain.matricula.service.MatriculaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Testes unitários do matricula controller")
@ExtendWith(MockitoExtension.class)
public class MatriculaControllerTest {

    @Mock
    private MatriculaService matriculaService;
    @InjectMocks
    private MatriculaController matriculaController;

    @Test
    @DisplayName("Deve cadastrar uma nova matrícula com sucesso e retornar status 201 Created")
    void deveCadastrarMatriculaComSucesso() {
        var dto = new MatriculaCreateDTO(
                1L,
                1L,
                1L,
                LocalDate.of(2025, 5, 3)
        );
        var resumoDTO = new MatriculaResumoDTO(
                10L,
                LocalDate.of(2025, 5, 3),
                1L,
                "João Pedro",
                1L,
                "Java para todos",
                1L,
                "JAVA-052025",
                StatusMatricula.ATIVA);

        when(matriculaService.cadastrarMatricula(dto)).thenReturn(resumoDTO);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        ResponseEntity<MatriculaResumoDTO> response = matriculaController.cadastrar(dto, uriBuilder);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED, response.getStatusCode()),
                () -> assertEquals(resumoDTO, response.getBody()),
                () -> assertTrue(response.getHeaders().getLocation().toString().contains("/matriculas/10")),
                () -> verify(matriculaService).cadastrarMatricula(dto)
        );
    }

    @Test
    @DisplayName("Deve cadastrar matrícula com data atual e carga horária minima")
    void deveCadastrarMatriculaComDataAtual() {
        var dto = new MatriculaCreateDTO(1L, 2L, null, LocalDate.now());

        var resumo = new MatriculaResumoDTO(
                10L,
                dto.dataMatricula(),
                1L,
                "João da Silva",
                2L,
                "Java avançado",
                null,
                null,
                StatusMatricula.ATIVA
        );

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");
        when(matriculaService.cadastrarMatricula(dto)).thenReturn(resumo);

        var response = matriculaController.cadastrar(dto, uriBuilder);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED, response.getStatusCode()),
                () -> assertEquals(resumo, response.getBody()),
                () -> assertTrue(response.getHeaders().getLocation().toString().contains("/matriculas/10"))
        );
        verify(matriculaService).cadastrarMatricula(dto);
    }

    @Test
    @DisplayName("Deve lançar exceção quando aluno já estiver matriculado na turma")
    void deveLancarExcecaoQuandoAlunoJaMatriculado() {
        var dto = new MatriculaCreateDTO(
                5L,
                7L,
                1L,
                LocalDate.now()
        );

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");
        when(matriculaService.cadastrarMatricula(dto))
                .thenThrow(new ValidacaoException("Aluno já está matriculado nesta turma"));

        var ex = assertThrows(ValidacaoException.class, () ->
                matriculaController.cadastrar(dto, uriBuilder)
        );

        assertEquals("Aluno já está matriculado nesta turma", ex.getMessage());
        verify(matriculaService).cadastrarMatricula(dto);
    }

    @Test
    @DisplayName("Deve retornar os detalhes de uma matrícula por ID com sucesso")
    void deveDetalharMatriculaPorIdComSucesso() {
        Long id = 1L;
        var detalhes = new MatriculaDetalhesDTO(
                10L,
                LocalDate.of(2025, 5, 3),
                LocalDate.of(2025, 8, 3),
                new BigDecimal("8"),
                StatusMatricula.ATIVA,
                null,
                1L,
                "João Pedro",
                1L,
                "Java para todos",
                3L,
                "JAVA-052025");

        when(matriculaService.detalharPorId(id)).thenReturn(detalhes);

        ResponseEntity<MatriculaDetalhesDTO> response = matriculaController.detalharPorId(id);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(detalhes, response.getBody())
        );
        verify(matriculaService).detalharPorId(id);
    }

    @Test
    @DisplayName("Deve buscar matrículas por nome do aluno e retornar uma lista")
    void deveBuscarMatriculasPorNomeDoAlunoComSucesso() {
        String nome = "João";
        List<MatriculaResumoDTO> lista = List.of(
                new MatriculaResumoDTO(
                        5L,
                        LocalDate.of(2025, 2, 3),
                        1L,
                        "João Pedro",
                        1L,
                        "Java para todos",
                        1L,
                        "JAVA-052025",
                        StatusMatricula.ATIVA),

                new MatriculaResumoDTO(
                        6L,
                        LocalDate.of(2025, 3, 3),
                        2L,
                        "João Vazques",
                        2L,
                        "Java para todos",
                        2L,
                        "JAVA-052025",
                        StatusMatricula.ATIVA));

        when(matriculaService.buscarPorNomeDoAluno(nome)).thenReturn(lista);

        ResponseEntity<List<MatriculaResumoDTO>> response = matriculaController.buscarPorNome(nome);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(2, response.getBody().size())
        );
        verify(matriculaService).buscarPorNomeDoAluno(nome);
    }

    @Test
    @DisplayName("Deve listar todas as matrículas com sucesso")
    void deveListarTodasMatriculasComSucesso() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("dataMatricula").ascending());
        List<MatriculaResumoDTO> lista = List.of(
                new MatriculaResumoDTO(
                        5L,
                        LocalDate.of(2025, 2, 3),
                        1L,
                        "João Pedro",
                        1L,
                        "Java para todos",
                        1L,
                        "JAVA-052025",
                        StatusMatricula.ATIVA),

                new MatriculaResumoDTO(
                        6L,
                        LocalDate.of(2025, 3, 3),
                        2L,
                        "João Pedro",
                        2L,
                        "Java para todos",
                        2L,
                        "JAVA-052025",
                        StatusMatricula.ATIVA));
        Page<MatriculaResumoDTO> page = new PageImpl<>(lista, pageable, lista.size());

        when(matriculaService.buscarTodasMatriculas(pageable)).thenReturn(page);

        Page<MatriculaResumoDTO> resultado = matriculaController.listarTodos(pageable);

        assertAll(
                () -> assertEquals(2, resultado.getContent().size()),
                () -> assertEquals("João Pedro", resultado.getContent().getFirst().nomeAluno())
        );
        verify(matriculaService).buscarTodasMatriculas(pageable);
    }

    @Test
    @DisplayName("Deve concluir uma matrícula com sucesso")
    void deveConcluirMatriculaComSucesso() {
        Long id = 1L;
        BigDecimal nota = new BigDecimal("8.5");
        var dto = new ConcluirMatriculaDTO(nota);
        var resumo = new MatriculaResumoDTO(
                id,
                LocalDate.of(2025, 3, 20),
                2L,
                "João Pedro",
                2L,
                "Java completo",
                2L,
                "JAVA-082025",
                StatusMatricula.CONCLUIDA);

        when(matriculaService.concluirMatricula(id, nota)).thenReturn(resumo);

        ResponseEntity<MatriculaResumoDTO> response = matriculaController.concluir(id, dto);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(resumo, response.getBody())
        );
        verify(matriculaService).concluirMatricula(id, nota);
    }

    @Test
    @DisplayName("Deve trancar uma matrícula com sucesso")
    void deveTrancarMatriculaComSucesso() {
        Long id = 2L;
        var resumo = new MatriculaResumoDTO(
                id,
                LocalDate.of(2025, 4, 20),
                4L,
                "Lucas Souza",
                3L,
                "Python completo",
                2L,
                "PYTHON-062025",
                StatusMatricula.ATIVA);

        when(matriculaService.trancarMatricula(id)).thenReturn(resumo);

        ResponseEntity<MatriculaResumoDTO> response = matriculaController.trancar(id);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(resumo, response.getBody())
        );
        verify(matriculaService).trancarMatricula(id);
    }

    @Test
    @DisplayName("Deve reativar uma matrícula com sucesso")
    void deveReativarMatriculaComSucesso() {
        Long id = 3L;
        var resumo = new MatriculaResumoDTO(
                id,
                LocalDate.of(2025, 4, 20),
                5L,
                "Lucas Souza",
                4L,
                "Curso C#",
                4L,
                "C#-062025",
                StatusMatricula.ATIVA);

        when(matriculaService.reativarMatricula(id)).thenReturn(resumo);

        ResponseEntity<MatriculaResumoDTO> response = matriculaController.reativar(id);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(resumo, response.getBody())
        );
        verify(matriculaService).reativarMatricula(id);
    }

    @Test
    @DisplayName("Deve cancelar uma matrícula com sucesso")
    void deveCancelarMatriculaComSucesso() {
        Long id = 4L;
        var motivo = MotivoCancelamento.DESISTENCIA;
        var dto = new CancelarMatriculaDTO(motivo);
        var resumo = new MatriculaResumoDTO(
                id,
                LocalDate.of(2025, 4, 20),
                5L,
                "Lucas Souza",
                4L,
                "Curso C#",
                4L,
                "C#-062025",
                StatusMatricula.ATIVA);

        when(matriculaService.cancelarMatricula(id, motivo)).thenReturn(resumo);

        ResponseEntity<MatriculaResumoDTO> response = matriculaController.cancelar(id, dto);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(resumo, response.getBody())
        );
        verify(matriculaService).cancelarMatricula(id, motivo);
    }
}
