package com.edutech.api.controller;

import com.edutech.api.domain.curso.dto.CursoCreateDTO;
import com.edutech.api.domain.curso.dto.CursoDetalhesDTO;
import com.edutech.api.domain.curso.dto.CursoResumoDTO;
import com.edutech.api.domain.curso.dto.CursoUpdateDTO;
import com.edutech.api.domain.curso.enums.CategoriaCurso;
import com.edutech.api.domain.curso.enums.NivelCurso;
import com.edutech.api.domain.curso.enums.StatusCurso;
import com.edutech.api.domain.curso.service.CursoService;
import com.edutech.api.domain.exception.ValidacaoException;
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

import java.net.URI;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Testes unitários do curso controller")
@ExtendWith(MockitoExtension.class)
class CursoControllerTest {

    @Mock
    private CursoService cursoService;

    @InjectMocks
    private CursoController cursoController;

    @Test
    @DisplayName("Deve cadastrar um novo curso e retornar status 201 Created")
    void deveCadastrarCursoERetornarCreated() {
        var dto = new CursoCreateDTO(
                "Curso Java",
                "Curso de Java completo",
                35,
                2,
                NivelCurso.INTERMEDIARIO,
                CategoriaCurso.PROGRAMACAO);

        var resumo = new CursoResumoDTO(
                1L,
                "Curso Java",
                StatusCurso.ATIVO,
                35,
                NivelCurso.INTERMEDIARIO,
                CategoriaCurso.PROGRAMACAO);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");
        when(cursoService.cadastrarCurso(dto)).thenReturn(resumo);

        ResponseEntity<CursoResumoDTO> response = cursoController.cadastrar(dto, uriBuilder);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED, response.getStatusCode()),
                () -> assertEquals(resumo, response.getBody()),
                () -> assertEquals(URI.create("http://localhost/cursos/1"), response.getHeaders().getLocation())
        );
        verify(cursoService).cadastrarCurso(dto);
    }

    @Test
    @DisplayName("Deve cadastrar curso com nome de 1000 caracteres")
    void deveCadastrarCursoComNomeMaximo() {
        String nome = "A".repeat(1000);
        var dto = new CursoCreateDTO(
                nome,
                "Curso de Java completo",
                35,
                2,
                NivelCurso.INTERMEDIARIO,
                CategoriaCurso.PROGRAMACAO);

        var resumo = new CursoResumoDTO(
                1L,
                nome,
                StatusCurso.ATIVO,
                35,
                NivelCurso.INTERMEDIARIO,
                CategoriaCurso.PROGRAMACAO);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");
        when(cursoService.cadastrarCurso(dto)).thenReturn(resumo);

        var response = cursoController.cadastrar(dto, uriBuilder);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED, response.getStatusCode()),
                () -> assertEquals(resumo, response.getBody()),
                () -> assertTrue(response.getHeaders().getLocation().toString().contains("/cursos/1")),
                () -> verify(cursoService).cadastrarCurso(dto)
        );
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar curso com nome já existente")
    void deveLancarExcecaoAoCadastrarCursoDuplicado() {
        var dto = new CursoCreateDTO(
                "Curso Java",
                "Curso de Java completo",
                35,
                2,
                NivelCurso.INTERMEDIARIO,
                CategoriaCurso.PROGRAMACAO);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        when(cursoService.cadastrarCurso(dto))
                .thenThrow(new ValidacaoException("Já existe um curso com esse nome"));

        var ex = assertThrows(ValidacaoException.class, () ->
                cursoController.cadastrar(dto, uriBuilder)
        );

        assertEquals("Já existe um curso com esse nome", ex.getMessage());
        verify(cursoService).cadastrarCurso(dto);
    }

    @Test
    @DisplayName("Deve atualizar os dados de um curso existente e retornar status 200 OK")
    void deveAtualizarCursoERetornarOk() {
        Long id = 1L;
        var dto = new CursoUpdateDTO(
                "Curso Atualizado",
                "Descrição atualizada",
                50,
                2,
                NivelCurso.INTERMEDIARIO,
                CategoriaCurso.PROGRAMACAO);

        var atualizado = new CursoResumoDTO(
                id,
                "Curso Atualizado",
                StatusCurso.ATIVO,
                50,
                NivelCurso.INTERMEDIARIO,
                CategoriaCurso.PROGRAMACAO);

        when(cursoService.atualizarCurso(id, dto)).thenReturn(atualizado);

        ResponseEntity<CursoResumoDTO> response = cursoController.atualizar(id, dto);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(atualizado, response.getBody())
        );

        verify(cursoService).atualizarCurso(id, dto);
    }

    @Test
    @DisplayName("Deve atualizar curso com carga horária mínima")
    void deveAtualizarCursoComCargaHorariaMinima() {
        Long id = 1L;
        var dto = new CursoUpdateDTO(
                "Curso Atualizado",
                "Descrição atualizada",
                1,
                2,
                NivelCurso.INTERMEDIARIO,
                CategoriaCurso.PROGRAMACAO);

        var resumo = new CursoResumoDTO(
                id,
                "Curso Atualizado",
                StatusCurso.ATIVO,
                1,
                NivelCurso.INTERMEDIARIO,
                CategoriaCurso.PROGRAMACAO);

        when(cursoService.atualizarCurso(id, dto)).thenReturn(resumo);

        var response = cursoController.atualizar(id, dto);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(resumo, response.getBody()),
                () -> verify(cursoService).atualizarCurso(id, dto)
        );
    }

    @Test
    @DisplayName("Deve retornar todos os cursos paginados")
    void deveRetornarTodosCursosPaginados() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nome"));
        List<CursoResumoDTO> lista = List.of(
                new CursoResumoDTO(
                        1L,
                        "Curso A",
                        StatusCurso.ATIVO,
                        50,
                        NivelCurso.INTERMEDIARIO,
                        CategoriaCurso.PROGRAMACAO),
                new CursoResumoDTO(
                        1L,
                        "Curso B",
                        StatusCurso.ATIVO,
                        70,
                        NivelCurso.AVANCADO,
                        CategoriaCurso.PROGRAMACAO)
        );
        Page<CursoResumoDTO> page = new PageImpl<>(lista, pageable, lista.size());

        when(cursoService.buscarTodosCursos(pageable)).thenReturn(page);

        ResponseEntity<Page<CursoResumoDTO>> response = cursoController.buscarTodosCursos(pageable);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(page, response.getBody())
        );

        verify(cursoService).buscarTodosCursos(pageable);
    }

    @Test
    @DisplayName("Deve buscar um curso por ID e retornar status 200 OK")
    void deveBuscarCursoPorIdERetornarOk() {
        Long id = 1L;
        CursoResumoDTO resumoMock = new CursoResumoDTO(
                id,
                "Curso Java",
                StatusCurso.ATIVO,
                40,
                NivelCurso.INTERMEDIARIO,
                CategoriaCurso.PROGRAMACAO);

        when(cursoService.buscarPorId(id)).thenReturn(resumoMock);

        ResponseEntity<CursoResumoDTO> response = cursoController.buscarPorId(id);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(resumoMock, response.getBody())
        );

        verify(cursoService).buscarPorId(id);
    }

    @Test
    @DisplayName("Deve retornar os detalhes completos de um curso por ID e retornar status 200 OK")
    void deveDetalharCursoPorIdERetornarOk() {
        Long id = 1L;
        List<String> professores = List.of("Professor A", "Professor B");

        CursoDetalhesDTO detalhesMock = new CursoDetalhesDTO(
                id,
                "Curso Java",
                "Descrição do curso Java",
                40,
                2,
                StatusCurso.ATIVO,
                NivelCurso.INTERMEDIARIO,
                CategoriaCurso.PROGRAMACAO,
                professores
        );

        when(cursoService.detalharPorId(id)).thenReturn(detalhesMock);

        ResponseEntity<CursoDetalhesDTO> response = cursoController.detalharPorId(id);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(detalhesMock, response.getBody())
        );

        verify(cursoService).detalharPorId(id);
    }

    @Test
    @DisplayName("Deve buscar cursos por intervalo de carga horária e retornar uma lista")
    void deveBuscarCursosPorCargaHorariaIntervaloERetornarLista() {
        Integer min = 20;
        Integer max = 50;

        List<CursoResumoDTO> listaMock = List.of(
                new CursoResumoDTO(
                        1L,
                        "Curso A",
                        StatusCurso.ATIVO,
                        50,
                        NivelCurso.INTERMEDIARIO,
                        CategoriaCurso.PROGRAMACAO),
                new CursoResumoDTO(
                        1L,
                        "Curso B",
                        StatusCurso.ATIVO,
                        70,
                        NivelCurso.AVANCADO,
                        CategoriaCurso.PROGRAMACAO)
        );

        when(cursoService.buscarPorCargaHorariaIntervalo(min, max)).thenReturn(listaMock);

        ResponseEntity<List<CursoResumoDTO>> response = cursoController.buscarPorCargaHorariaIntervalo(min, max);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(listaMock, response.getBody())
        );

        verify(cursoService).buscarPorCargaHorariaIntervalo(min, max);
    }

    @Test
    @DisplayName("Deve buscar cursos por nível de dificuldade e retornar uma lista")
    void deveBuscarCursosPorNivelERetornarLista() {
        NivelCurso nivel = NivelCurso.INTERMEDIARIO;

        List<CursoResumoDTO> listaMock = List.of(
                new CursoResumoDTO(
                        1L,
                        "Curso C",
                        StatusCurso.ATIVO,
                        40,
                        nivel, CategoriaCurso.PROGRAMACAO),

                new CursoResumoDTO(
                        2L,
                        "Curso D",
                        StatusCurso.ATIVO,
                        35,
                        nivel,
                        CategoriaCurso.BANCO_DADOS)
        );

        when(cursoService.buscarPorNivel(nivel)).thenReturn(listaMock);

        ResponseEntity<List<CursoResumoDTO>> response = cursoController.buscarPorNivel(nivel);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(listaMock, response.getBody());
        assertNotNull(response.getBody());

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(listaMock, response.getBody())
        );

        verify(cursoService).buscarPorNivel(nivel);
    }

    @Test
    @DisplayName("Deve buscar cursos por nome e retornar uma lista")
    void deveBuscarCursoPorNomeERetornarOk() {
        String nome = "Curso Java";

        CursoResumoDTO cursoResumoDTO = new CursoResumoDTO(
                1L,
                nome,
                StatusCurso.ATIVO,
                40,
                NivelCurso.INTERMEDIARIO,
                CategoriaCurso.PROGRAMACAO
        );

        when(cursoService.buscarPorNome(nome)).thenReturn(cursoResumoDTO);

        ResponseEntity<CursoResumoDTO> response = cursoController.buscarPorNome(nome);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(nome, response.getBody().nome())
        );

        verify(cursoService).buscarPorNome(nome);
    }

    @Test
    @DisplayName("Deve ativar um curso e retornar status 200 OK")
    void deveAtivarCursoERetornarOk() {
        Long id = 1L;

        doNothing().when(cursoService).ativarCurso(id);

        ResponseEntity<Void> response = cursoController.ativarCurso(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(cursoService).ativarCurso(id);
    }

    @Test
    @DisplayName("Deve inativar um curso e retornar status 200 OK")
    void deveInativarCursoERetornarOk() {
        Long id = 2L;

        doNothing().when(cursoService).inativarCurso(id);

        ResponseEntity<Void> response = cursoController.inativarCurso(id);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(cursoService).inativarCurso(id);
    }

    @Test
    @DisplayName("Deve vincular um professor a um curso com sucesso")
    void deveVincularProfessorAoCursoComSucesso() {
        Long cursoId = 1L;
        Long professorId = 10L;

        doNothing().when(cursoService).vincularProfessor(cursoId, professorId);

        ResponseEntity<Void> response = cursoController.vincularProfessor(cursoId, professorId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(cursoService).vincularProfessor(cursoId, professorId);
    }

    @Test
    @DisplayName("Deve desvincular um professor de um curso com sucesso")
    void deveDesvincularProfessorDoCursoComSucesso() {
        Long cursoId = 1L;
        Long professorId = 10L;

        doNothing().when(cursoService).desvincularProfessor(cursoId, professorId);

        ResponseEntity<Void> response = cursoController.desvincularProfessor(cursoId, professorId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        verify(cursoService).desvincularProfessor(cursoId, professorId);
    }

    @Test
    @DisplayName("Deve listar os cursos associados a um professor com sucesso")
    void deveListarCursosDoProfessorComSucesso() {
        Long professorId = 10L;

        List<CursoResumoDTO> cursos = List.of(
                new CursoResumoDTO(
                        1L,
                        "Curso Java",
                        StatusCurso.ATIVO,
                        40,
                        NivelCurso.INTERMEDIARIO,
                        CategoriaCurso.PROGRAMACAO),

                new CursoResumoDTO(
                        2L,
                        "Curso Python",
                        StatusCurso.ATIVO,
                        30,
                        NivelCurso.BASICO,
                        CategoriaCurso.PROGRAMACAO)
        );

        when(cursoService.listarCursosDoProfessor(professorId)).thenReturn(cursos);

        ResponseEntity<List<CursoResumoDTO>> response = cursoController.listarCursosDoProfessor(professorId);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(2, response.getBody().size()),
                () -> assertEquals("Curso Java", response.getBody().get(0).nome())
        );
        verify(cursoService).listarCursosDoProfessor(professorId);
    }

}
