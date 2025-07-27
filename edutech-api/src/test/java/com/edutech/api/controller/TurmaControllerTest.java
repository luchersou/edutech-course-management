package com.edutech.api.controller;

import com.edutech.api.domain.enums.Modalidade;
import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.turma.dto.TurmaCreateDTO;
import com.edutech.api.domain.turma.dto.TurmaDetalhesDTO;
import com.edutech.api.domain.turma.dto.TurmaResumoDTO;
import com.edutech.api.domain.turma.dto.TurmaUpdateDTO;
import com.edutech.api.domain.turma.enums.StatusTurma;
import com.edutech.api.domain.turma.service.TurmaService;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Testes unitários do Turma controller")
@ExtendWith(MockitoExtension.class)
class TurmaControllerTest {

    @Mock
    private TurmaService turmaService;
    @InjectMocks
    private TurmaController turmaController;

    @Test
    @DisplayName("Deve cadastrar uma nova turma com sucesso e retornar status 201 Created")
    void deveCadastrarTurmaComSucesso() {
        var dto = new TurmaCreateDTO(
                "JAVA-052025",
                LocalDate.of(2025, 05, 20),
                LocalDate.of(2025, 07, 20),
                LocalTime.of(8,0),
                LocalTime.of(10,0),
                30,
                Modalidade.PRESENCIAL
        );

        var resumo = new TurmaResumoDTO(
                1L,
                "JAVA-052025",
                LocalDate.of(2025, 05, 20),
                LocalDate.of(2025, 07, 20),
                StatusTurma.ABERTA
        );

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");
        when(turmaService.cadastrarTurma(any())).thenReturn(resumo);

        ResponseEntity<TurmaResumoDTO> response = turmaController.cadastrar(dto, uriBuilder);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED, response.getStatusCode()),
                () -> assertEquals(resumo, response.getBody())
        );
    }

    @Test
    @DisplayName("Deve cadastrar turma com horário no limite (00:00 às 23:59)")
    void deveCadastrarTurmaComHorarioNoLimite() {
        var dto = new TurmaCreateDTO(
                "JAVA-NOTURNO",
                LocalDate.of(2025, 05, 20),
                LocalDate.of(2025, 07, 20),
                LocalTime.of(0, 0),
                LocalTime.of(23, 59),
                30,
                Modalidade.PRESENCIAL
        );

        var resumo = new TurmaResumoDTO(
                1L,
                "JAVA-NOTURNO",
                LocalDate.of(2025, 05, 20),
                LocalDate.of(2025, 07, 20),
                StatusTurma.ABERTA
        );

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");
        when(turmaService.cadastrarTurma(dto)).thenReturn(resumo);

        ResponseEntity<TurmaResumoDTO> response = turmaController.cadastrar(dto, uriBuilder);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED, response.getStatusCode()),
                () -> assertEquals(resumo, response.getBody())
        );
        verify(turmaService).cadastrarTurma(dto);
    }

    @Test
    @DisplayName("Deve propagar ValidacaoException quando data início é posterior a data fim")
    void ValidacaoExceptionQuandoDataInicioPosteriorDataFim() {
        var dto = new TurmaCreateDTO(
                "JAVA-INVALIDA",
                LocalDate.of(2025, 07, 20),
                LocalDate.of(2025, 05, 20),
                LocalTime.of(8, 0),
                LocalTime.of(10, 0),
                30,
                Modalidade.PRESENCIAL
        );

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");
        when(turmaService.cadastrarTurma(dto))
                .thenThrow(new ValidacaoException("Data inicio deve ser anterior a data fim"));

        ValidacaoException exception = assertThrows(
                ValidacaoException.class,
                () -> turmaController.cadastrar(dto, uriBuilder)
        );

        assertEquals("Data inicio deve ser anterior a data fim", exception.getMessage());
        verify(turmaService).cadastrarTurma(dto);
    }

    @Test
    @DisplayName("Deve lançar exceção quando data inicio é igual a data fim em cadastro de turma")
    void deveLancarExcecaoQuandoDataInicioIgualDataFim() {
        LocalDate data = LocalDate.of(2024, 12, 1);

        var dto = new TurmaCreateDTO(
                "JAVA-052025",
                data,
                data,
                LocalTime.of(8, 0),
                LocalTime.of(10, 0),
                30,
                Modalidade.PRESENCIAL
        );

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        when(turmaService.cadastrarTurma(dto))
                .thenThrow(new ValidacaoException("Data inicio deve ser anterior a data fim"));

        ValidacaoException exception = assertThrows(
                ValidacaoException.class,
                () -> turmaController.cadastrar(dto, uriBuilder)
        );

        assertEquals("Data inicio deve ser anterior a data fim", exception.getMessage());
        verify(turmaService).cadastrarTurma(dto);
    }

    @Test
    @DisplayName("Deve atualizar turma com sucesso e retornar status 200 OK")
    void deveAtualizarTurmaComSucesso() {
        Long id = 1L;
        var dto = new TurmaUpdateDTO(
                "JAVA-UPDATE-2025",
                LocalDate.of(2025, 6, 15),
                LocalDate.of(2025, 8, 15),
                LocalTime.of(9, 0),
                LocalTime.of(11, 0),
                25,
                Modalidade.HIBRIDO
        );

        var turmaAtualizada = new TurmaResumoDTO(
                id,
                "JAVA-UPDATE-2025",
                LocalDate.of(2025, 6, 15),
                LocalDate.of(2025, 8, 15),
                StatusTurma.ABERTA
        );

        when(turmaService.atualizarTurma(id, dto)).thenReturn(turmaAtualizada);

        ResponseEntity<TurmaResumoDTO> response = turmaController.atualizar(id, dto);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(turmaAtualizada, response.getBody())
        );
        verify(turmaService).atualizarTurma(id, dto);
    }

    @Test
    @DisplayName("Deve atualizar apenas um campo especifico")
    void deveAtualizarApenasUmCampo() {
        Long id = 1L;
        var dto = new TurmaUpdateDTO(
                null, null, null, null, null,
                50,
                null
        );

        var turmaAtualizada = new TurmaResumoDTO(
                id,
                "JAVA-ORIGINAL",
                LocalDate.of(2025, 5, 20),
                LocalDate.of(2025, 7, 20),
                StatusTurma.ABERTA
        );

        when(turmaService.atualizarTurma(id, dto)).thenReturn(turmaAtualizada);

        ResponseEntity<TurmaResumoDTO> response = turmaController.atualizar(id, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(turmaService).atualizarTurma(id, dto);
    }

    @Test
    @DisplayName("Deve atualizar com datas no limite (datas extremas)")
    void deveAtualizarComDatasExtremas() {
        Long id = 1L;
        var dto = new TurmaUpdateDTO(
                "JAVA-EXTREMO",
                LocalDate.of(1970, 1, 1),
                LocalDate.of(2099, 12, 31),
                LocalTime.of(0, 0),
                LocalTime.of(23, 59),
                1,
                Modalidade.PRESENCIAL
        );

        var turmaAtualizada = new TurmaResumoDTO(
                id, "JAVA-EXTREMO",
                LocalDate.of(1970, 1, 1),
                LocalDate.of(2099, 12, 31),
                StatusTurma.ABERTA
        );

        when(turmaService.atualizarTurma(id, dto)).thenReturn(turmaAtualizada);

        ResponseEntity<TurmaResumoDTO> response = turmaController.atualizar(id, dto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(turmaService).atualizarTurma(id, dto);
    }

    @Test
    @DisplayName("Deve lançar exceção quando curso não existir")
    void ExcecaoQuandoCursoNaoExistir() {
        Long id = 1L;
        var dto = new TurmaUpdateDTO(
                "PYTHON-999",
                null,
                null,
                null,
                null,
                null,
                null);

        when(turmaService.atualizarTurma(id, dto))
                .thenThrow(new ValidacaoException("Curso não encontrado"));

        ValidacaoException exception = assertThrows(
                ValidacaoException.class,
                () -> turmaController.atualizar(id, dto)
        );

        assertAll(
                () -> assertEquals("Curso não encontrado", exception.getMessage()),
                () -> verify(turmaService).atualizarTurma(id, dto)
        );
    }

    @Test
    @DisplayName("Deve retornar os detalhes de uma turma por ID com sucesso e retornar status 200 OK")
    void deveDetalharTurmaPorIdComSucesso() {
        Long id = 1L;
        TurmaDetalhesDTO detalhes = new TurmaDetalhesDTO(
                id,
                "JAVA-092025",
                LocalDate.of(2025, 9, 1),
                LocalDate.of(2025, 12, 15),
                LocalTime.of(8, 0),
                LocalTime.of(12, 0),
                Modalidade.EAD,
                30,
                30,
                StatusTurma.ABERTA
        );

        when(turmaService.detalharPorId(id)).thenReturn(detalhes);

        ResponseEntity<TurmaDetalhesDTO> response = turmaController.detalharPorId(id);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(detalhes, response.getBody())
        );
        verify(turmaService).detalharPorId(id);
    }

    @Test
    @DisplayName("Deve buscar uma turma por código com sucesso e retornar status 200 OK")
    void deveBuscarTurmaPorCodigoComSucesso() {
        Long id = 1L;
        String codigo = "JAVA-092025";
        TurmaResumoDTO resumo = new TurmaResumoDTO(
                id,
                codigo,
                LocalDate.of(2025, 9, 1),
                LocalDate.of(2025, 12, 15),
                StatusTurma.ABERTA
        );

        when(turmaService.buscarPorCodigo(codigo)).thenReturn(resumo);

        ResponseEntity<TurmaResumoDTO> response = turmaController.buscarPorCodigo(codigo);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(resumo, response.getBody())
        );
        verify(turmaService).buscarPorCodigo(codigo);
    }

    @Test
    @DisplayName("Deve listar todas as turmas com sucesso e retornar status 200 OK")
    void deveListarTodasTurmasComSucesso() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("dataInicio").ascending());
        List<TurmaResumoDTO> turmas = List.of(
                new TurmaResumoDTO(
                        1L,
                        "Turma A",
                        LocalDate.of(2025,4,20),
                        LocalDate.of(2025,6,20),
                        StatusTurma.ABERTA),

                new TurmaResumoDTO(
                        2L,
                        "Turma B",
                        LocalDate.of(2025,5,10),
                        LocalDate.of(2025,7,10),
                        StatusTurma.ABERTA)
        );
        Page<TurmaResumoDTO> page = new PageImpl<>(turmas, pageable, turmas.size());

        when(turmaService.buscarTodasTurmas(pageable)).thenReturn(page);

        Page<TurmaResumoDTO> resultado = turmaController.buscarTodasTurmas(pageable);

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(2, resultado.getTotalElements()),
                () -> assertEquals("Turma A", resultado.getContent().getFirst().codigo())
        );
        verify(turmaService).buscarTodasTurmas(pageable);
    }

    @Test
    @DisplayName("Deve iniciar uma turma com sucesso")
    void deveIniciarTurmaComSucesso() {
        Long turmaId = 1L;

        ResponseEntity<Void> response = turmaController.iniciarTurma(turmaId);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNull(response.getBody())
        );
        verify(turmaService).iniciarTurma(turmaId);
    }

    @Test
    @DisplayName("Deve concluir uma turma com sucesso e retornar status 200 OK")
    void deveConcluirTurmaComSucesso() {
        Long turmaId = 2L;

        ResponseEntity<Void> response = turmaController.concluirTurma(turmaId);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNull(response.getBody())
        );
        verify(turmaService).concluirTurma(turmaId);
    }

    @Test
    @DisplayName("Deve cancelar uma turma com sucesso e retornar status 200 OK")
    void deveCancelarTurmaComSucesso() {
        Long id = 3L;

        ResponseEntity<Void> response = turmaController.cancelarTurma(id);

        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()),
                () -> assertNull(response.getBody())
        );
        verify(turmaService).cancelarTurma(id);
    }

    /**
     * Vincular/Desvincular professor de turma
     */
    @Test
    @DisplayName("Deve vincular um professor a uma turma com sucesso")
    void deveVincularProfessorComSucesso() {
        Long turmaId = 1L;
        Long professorId = 10L;

        ResponseEntity<Void> response = turmaController.vincularProfessor(turmaId, professorId);

        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()),
                () -> assertNull(response.getBody())
        );
        verify(turmaService).vincularProfessor(turmaId, professorId);
    }

    @Test
    @DisplayName("Deve desvincular um professor de uma turma com sucesso")
    void deveDesvincularProfessorComSucesso() {
        Long turmaId = 2L;
        Long professorId = 11L;

        ResponseEntity<Void> response = turmaController.desvincularProfessor(turmaId, professorId);

        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()),
                () -> assertNull(response.getBody()),
                () -> assertTrue(response.getHeaders().isEmpty(), "Headers devem estar vazios")
        );
        verify(turmaService).desvincularProfessor(turmaId, professorId);
    }

    /**
     * Vincular/Desvincular curso de turma
     */
    @Test
    @DisplayName("Deve vincular um curso a uma turma com sucesso")
    void deveVincularCursoComSucesso() {
        Long turmaId = 3L;
        Long cursoId = 200L;

        ResponseEntity<Void> response = turmaController.vincularCurso(turmaId, cursoId);

        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()),
                () -> assertNull(response.getBody())
        );
        verify(turmaService).vincularCurso(turmaId, cursoId);
    }

    @Test
    @DisplayName("Deve desvincular um curso de uma turma com sucesso")
    void deveDesvincularCursoComSucesso() {
        Long turmaId = 4L;
        Long cursoId = 12L;

        ResponseEntity<Void> response = turmaController.desvincularCurso(turmaId, cursoId);

        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()),
                () -> assertNull(response.getBody())
        );
        verify(turmaService).desvincularCurso(turmaId, cursoId);
    }
}
