package com.edutech.api.controller;

import com.edutech.api.domain.endereco.dto.DadosEnderecoDTO;
import com.edutech.api.domain.enums.Modalidade;
import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.professor.dto.ProfessorCreateDTO;
import com.edutech.api.domain.professor.dto.ProfessorDetalhesDTO;
import com.edutech.api.domain.professor.dto.ProfessorResumoDTO;
import com.edutech.api.domain.professor.dto.ProfessorUpdateDTO;
import com.edutech.api.domain.professor.enums.StatusProfessor;
import com.edutech.api.domain.professor.service.ProfessorService;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Testes unitários do professor controller")
@ExtendWith(MockitoExtension.class)
public class ProfessorControllerTest {

    @Mock
    private ProfessorService professorService;
    @InjectMocks
    private ProfessorController professorController;

    @Test
    @DisplayName("Deve cadastrar um novo professor com sucesso e retornar status 201 Created")
    void deveCadastrarProfessorComSucesso() {
        var dto = new ProfessorCreateDTO(
                "Lucas Herzinger",
                "lucas@email.com",
                LocalDate.of(1997, 5, 27),
                "(11)91234-5678",
                "12345678901",
                Modalidade.EAD,
                new DadosEnderecoDTO("Rua A", "Bairro B", "12345000", "São Paulo", "SP", "Apto 1", "13")
        );

        var resumo = new ProfessorResumoDTO(
                1L,
                "Lucas Souza",
                "lucas@email.com",
                Modalidade.EAD,
                StatusProfessor.ATIVO
        );

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        when(professorService.cadastrarProfessor(dto)).thenReturn(resumo);

        ResponseEntity<ProfessorResumoDTO> response = professorController.cadastrarProfessor(dto, uriBuilder);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED, response.getStatusCode()),
                () -> assertEquals(resumo, response.getBody()),
                () -> assertNotNull(response.getHeaders().getLocation()),
                () -> assertTrue(response.getHeaders().getLocation().toString().contains("/professores/1")),
                () -> assertEquals(dto.modalidade(), resumo.modalidade())
        );

        verify(professorService).cadastrarProfessor(dto);
    }

    @Test
    @DisplayName("Deve lançar ValidacaoException quando CPF já existe")
    void deveLancarExcecaoQuandoCpfDuplicado() {
        var dto = new ProfessorCreateDTO(
                "Lucas Herzinger",
                "lucas@email.com",
                LocalDate.of(2000, 5, 27),
                "(11)91234-5678",
                "12345678901",
                Modalidade.EAD,
                new DadosEnderecoDTO("Rua A", "Bairro B", "12345000", "São Paulo", "SP", "Apto 1", "13")
        );

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");
        when(professorService.cadastrarProfessor(dto))
                .thenThrow(new ValidacaoException("CPF já cadastrado"));

        var ex = assertThrows(ValidacaoException.class, () ->
                professorController.cadastrarProfessor(dto, uriBuilder)
        );

        assertEquals("CPF já cadastrado", ex.getMessage());
        verify(professorService).cadastrarProfessor(dto);
    }

    @Test
    @DisplayName("Deve atualizar os dados de um professor existente com sucesso e retornar status 200 OK")
    void deveAtualizarProfessorComSucesso() {
        Long id = 1L;
        ProfessorUpdateDTO dto = new ProfessorUpdateDTO(
                "Lucas Atualizado",
                "lucas@email.com",
                LocalDate.of(1997, 5, 27),
                "(11)91234-5678",
                StatusProfessor.ATIVO,
                Modalidade.PRESENCIAL,
                new DadosEnderecoDTO("Rua A", "Bairro B", "12345000", "São Paulo", "SP", "Apto 1", "13")
        );

        ProfessorResumoDTO atualizado = new ProfessorResumoDTO(
                1L,
                "Lucas Atualizado",
                "lucas@email.com",
                Modalidade.PRESENCIAL,
                StatusProfessor.ATIVO
        );

        when(professorService.atualizarProfessor(id, dto)).thenReturn(atualizado);

        ResponseEntity<ProfessorResumoDTO> response = professorController.atualizar(id, dto);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(atualizado, response.getBody()),
                () -> assertEquals(dto.modalidade(), atualizado.modalidade())
        );

        verify(professorService).atualizarProfessor(id, dto);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar professor inexistente")
    void deveLancarExcecaoAoAtualizarProfessorInexistente() {
        Long id = 1L;
        var dto = new ProfessorUpdateDTO(
                "Carlos",
                "carlos@email.com",
                LocalDate.of(1990,4,25),
                "(99)9999-9999",
                StatusProfessor.ATIVO,
                Modalidade.EAD,
                new DadosEnderecoDTO("Rua A", "Bairro B", "12345000", "São Paulo", "SP", "Apto 1", "13")
        );

        when(professorService.atualizarProfessor(id, dto))
                .thenThrow(new ValidacaoException("Professor não encontrado"));

        var ex = assertThrows(ValidacaoException.class, () ->
                professorController.atualizar(id, dto)
        );

        assertAll(
                () -> assertEquals("Professor não encontrado", ex.getMessage())
        );
        verify(professorService).atualizarProfessor(id, dto);
    }



    @Test
    @DisplayName("Deve buscar um professor por ID com sucesso e retornar status 200 OK")
    void deveBuscarProfessorPorIdComSucesso() {
        Long id = 1L;
        ProfessorResumoDTO resumo = new ProfessorResumoDTO(
                1L,
                "Lucas Atualizado",
                "lucas@email.com",
                Modalidade.PRESENCIAL,
                StatusProfessor.ATIVO
        );

        when(professorService.buscarPorId(id)).thenReturn(resumo);

        ResponseEntity<ProfessorResumoDTO> response = professorController.buscarProfessorPorId(id);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(resumo, response.getBody())
        );

        verify(professorService).buscarPorId(id);
    }

    @Test
    @DisplayName("Deve buscar professores por nome com sucesso e retornar uma lista")
    void deveBuscarProfessoresPorNomeComSucesso() {
        String nome = "Lucas";

        List<ProfessorResumoDTO> lista = List.of(
                new ProfessorResumoDTO(
                        1L,
                        "João Pedro",
                        "joao@email.com",
                        Modalidade.PRESENCIAL,
                        StatusProfessor.ATIVO
                ),
                new ProfessorResumoDTO(
                        2L,
                        "Lucas Souza",
                        "lucas@email.com",
                        Modalidade.HIBRIDO,
                        StatusProfessor.ATIVO));

        when(professorService.buscarProfessoresPorNome(nome)).thenReturn(lista);

        ResponseEntity<List<ProfessorResumoDTO>> response = professorController.buscarProfessoresPorNome(nome);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertNotNull(response.getBody()),
                () -> assertEquals(lista, response.getBody()),
                () -> assertEquals(2, response.getBody().size())
        );
        verify(professorService).buscarProfessoresPorNome(nome);
    }

    @Test
    @DisplayName("Deve buscar todos os professores por modalidade com sucesso")
    void deveBuscarProfessoresPorModalidadeComSucesso() {
        Modalidade modalidade = Modalidade.EAD;

        List<ProfessorResumoDTO> lista = List.of(
                new ProfessorResumoDTO(
                        3L,
                        "Pedro Santos",
                        "pedro@email.com",
                        Modalidade.EAD,
                        StatusProfessor.ATIVO));

        when(professorService.buscarProfessoresPorModalidade(modalidade)).thenReturn(lista);

        ResponseEntity<List<ProfessorResumoDTO>> response = professorController.buscarPorModalidade(modalidade);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(lista, response.getBody()),
                () -> assertEquals(1, response.getBody().size())
        );

        verify(professorService).buscarProfessoresPorModalidade(modalidade);
    }

    @Test
    @DisplayName("Deve buscar professores por nome com sucesso e retornar uma lista")
    void deveBuscarTodosProfessoresComSucesso() {
        Pageable pageable = PageRequest.of(0, 11, Sort.by("nome").ascending());
        List<ProfessorResumoDTO> lista = List.of(
                new ProfessorResumoDTO(1L, "Lucas Souza", "lucas@email.com", Modalidade.EAD, StatusProfessor.ATIVO),
                new ProfessorResumoDTO(2L, "Maria Silva", "maria@email.com", Modalidade.PRESENCIAL, StatusProfessor.ATIVO)
        );
        Page<ProfessorResumoDTO> page = new PageImpl<>(lista, pageable, lista.size());

        when(professorService.buscarTodosProfessores(pageable)).thenReturn(page);

        Page<ProfessorResumoDTO> resultado = professorController.buscarTodosProfessores(pageable);

        assertAll(
                () -> assertEquals(2, resultado.getContent().size()),
                () -> assertEquals("Lucas Souza", resultado.getContent().getFirst().nome()),
                () -> assertEquals("Maria Silva", resultado.getContent().get(1).nome())
        );
        verify(professorService).buscarTodosProfessores(pageable);
    }

    @Test
    @DisplayName("Deve retornar os detalhes completos de um professor por ID com sucesso")
    void deveDetalharProfessorComSucesso() {
        Long id = 1L;
        ProfessorDetalhesDTO detalhes = new ProfessorDetalhesDTO(
                id,
                "Lucas Souza",
                "lucas@email.com",
                LocalDate.of(1990, 5, 10),
                "(11)98765-4321",
                Modalidade.PRESENCIAL,
                StatusProfessor.ATIVO,
                new DadosEnderecoDTO("Rua A", "123", "Centro", "São Paulo", "SP", "01234567", null)
        );

        when(professorService.detalharProfessor(id)).thenReturn(detalhes);

        ResponseEntity<ProfessorDetalhesDTO> response = professorController.detalharProfessor(id);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(detalhes, response.getBody())
        );
        verify(professorService).detalharProfessor(id);
    }

    @Test
    @DisplayName("Deve excluir um professor com sucesso e retornar status 204 No Content")
    void deveExcluirProfessorComSucesso() {
        Long id = 1L;

        ResponseEntity<Void> response = professorController.excluir(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(professorService).excluirProfessor(id);
    }
}
