package com.edutech.api.controller;

import com.edutech.api.domain.aluno.dto.AlunoCreateDTO;
import com.edutech.api.domain.aluno.dto.AlunoDetalhesDTO;
import com.edutech.api.domain.aluno.dto.AlunoResumoDTO;
import com.edutech.api.domain.aluno.dto.AlunoUpdateDTO;
import com.edutech.api.domain.aluno.enums.StatusAluno;
import com.edutech.api.domain.aluno.service.AlunoService;
import com.edutech.api.domain.endereco.dto.DadosEnderecoDTO;
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

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("Testes unitários do aluno controller")
@ExtendWith(MockitoExtension.class)
class AlunoControllerTest {

    @InjectMocks
    private AlunoController alunoController;
    @Mock
    private AlunoService alunoService;

    @Test
    @DisplayName("Deve cadastrar um novo aluno e retornar status 201 Created")
    void deveCadastrarAlunoERetornarStatus201() {
        var dto = new AlunoCreateDTO(
                "Lucas Souza",
                "lucas@email.com",
                "11999999999",
                "12345678900",
                LocalDate.of(2002, 5, 30),
                new DadosEnderecoDTO("Rua A", "Bela Vista", "01311000", "São Paulo", "SP", "Aprt 101", "1578")
        );

        var alunoResumo = new AlunoResumoDTO(
                1L,
                dto.nome(),
                dto.email(),
                StatusAluno.ATIVO
        );

        when(alunoService.cadastrarAluno(dto)).thenReturn(alunoResumo);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        ResponseEntity<AlunoResumoDTO> resposta = alunoController.cadastrarAluno(dto, uriBuilder);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED, resposta.getStatusCode()),
                () -> assertEquals(alunoResumo, resposta.getBody()),
                () -> assertTrue(resposta.getHeaders().getLocation().toString().contains("/alunos/1"))
        );

        verify(alunoService).cadastrarAluno(dto);
    }

    @Test
    @DisplayName("Deve cadastrar aluno com nome contendo acento e hífen")
    void deveCadastrarAlunoComNomeComAcentoEHifen() {
        var dto = new AlunoCreateDTO(
                "José-Anthony López",
                "jose.lopez@email.com",
                "99999999999",
                "12345678900",
                LocalDate.of(2002, 5, 30),
                new DadosEnderecoDTO("Rua A", "Bela Vista", "01311000", "São Paulo", "SP", "Aprt 101", "1578")
        );

        var resumo = new AlunoResumoDTO(1L, "José-Anthony López", "jose.lopez@email.com", StatusAluno.ATIVO);

        when(alunoService.cadastrarAluno(dto)).thenReturn(resumo);

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");
        var response = alunoController.cadastrarAluno(dto, uriBuilder);

        assertAll(
                () -> assertEquals(HttpStatus.CREATED, response.getStatusCode()),
                () -> assertEquals(resumo, response.getBody()),
                () -> assertTrue(response.getHeaders().getLocation().toString().contains("/alunos/1")),
                () -> verify(alunoService).cadastrarAluno(dto)
        );
    }

    @Test
    @DisplayName("Deve lançar exceção ao cadastrar aluno com e-mail já cadastrado")
    void deveLancarExcecaoAoCadastrarAlunoEmailDuplicado() {
        var dto = new AlunoCreateDTO(
                "José-Anthony López",
                "jose.lopez@email.com",
                "99999999999",
                "12345678900",
                LocalDate.of(2002, 5, 30),
                new DadosEnderecoDTO("Rua A", "Bela Vista", "01311000", "São Paulo", "SP", "Aprt 101", "1578")
        );

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString("http://localhost");

        when(alunoService.cadastrarAluno(dto))
                .thenThrow(new ValidacaoException("E-mail já cadastrado"));

        var ex = assertThrows(ValidacaoException.class, () ->
                alunoController.cadastrarAluno(dto, uriBuilder)
        );

        assertEquals("E-mail já cadastrado", ex.getMessage());
        verify(alunoService).cadastrarAluno(dto);
    }

    @Test
    @DisplayName("Deve atualizar os dados de um aluno existente e retornar status 200 OK")
    void deveAtualizarAlunoERetornarStatus200() {
        Long alunoId = 1L;

        var updateDto = new AlunoUpdateDTO(
                "Lucas Pereira",
                "lucas.pereira@email.com",
                "(41)99960-4567",
                StatusAluno.ATIVO,
                LocalDate.of(2000, 5, 10),
                new DadosEnderecoDTO("Rua B", "Boa Vista", "01311000", "Curitiba", "PR", "Aprt 101", "1578")
        );

        var alunoResumoAtualizado = new AlunoResumoDTO(
                1l,
                "Lucas Pereira",
                "lucas.pereira@email.com",
                StatusAluno.ATIVO
        );

        when(alunoService.atualizarAluno(eq(alunoId), any(AlunoUpdateDTO.class)))
                .thenReturn(alunoResumoAtualizado);

        ResponseEntity<AlunoResumoDTO> response = alunoController.atualizar(alunoId, updateDto);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(alunoResumoAtualizado, response.getBody()),
                () -> assertNotNull(response.getBody())
        );

        verify(alunoService).atualizarAluno(eq(alunoId), eq(updateDto));
    }

    @Test
    @DisplayName("Deve retornar os detalhes de um aluno quando o ID for válido")
    void deveRetornarAlunoQuandoIdForValido() {
        Long id = 1L;
        AlunoResumoDTO alunoMock = new AlunoResumoDTO(id, "Lucas","lucas@gmail.com", StatusAluno.ATIVO);

        when(alunoService.buscarAlunoPorId(id)).thenReturn(alunoMock);

        ResponseEntity<AlunoResumoDTO> response = alunoController.buscarAlunoPorId(id);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(alunoMock, response.getBody())
        );
        verify(alunoService).buscarAlunoPorId(id);
    }

    @Test
    @DisplayName("Deve retornar uma lista de alunos quando o nome buscado for válido")
    void deveRetornarListaDeAlunosQuandoNomeForValido() {
        String nome = "Lucas";
        List<AlunoResumoDTO> listaMock = List.of(
                new AlunoResumoDTO(1L, "Lucas Silva","lucasSouza@email.com", StatusAluno.ATIVO),
                new AlunoResumoDTO(2L, "Lucas Souza", "lucasSouza@email.com", StatusAluno.ATIVO)
        );

        when(alunoService.buscarAlunoPorNome(nome)).thenReturn(listaMock);

        ResponseEntity<List<AlunoResumoDTO>> response = alunoController.buscarAlunosPorNome(nome);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(listaMock, response.getBody())
        );

        verify(alunoService).buscarAlunoPorNome(nome);
    }

    @Test
    @DisplayName("Deve retornar uma lista vazia quando o nome buscado não existir")
    void deveRetornarListaVaziaQuandoNomeNaoExistir() {
        String nome = "NomeInexistente";
        when(alunoService.buscarAlunoPorNome(nome)).thenReturn(List.of());

        ResponseEntity<List<AlunoResumoDTO>> response = alunoController.buscarAlunosPorNome(nome);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertTrue(response.getBody().isEmpty())
        );
    }

    @Test
    @DisplayName("Deve retornar alunos por status com paginação")
    void deveRetornarAlunosPorStatusComPaginacao() {
        StatusAluno status = StatusAluno.ATIVO;
        Pageable pageable = PageRequest.of(0, 10, Sort.by("nome").ascending());

        List<AlunoResumoDTO> lista = List.of(
                new AlunoResumoDTO(1L, "Lucas Silva","lucasSouza@email.com", StatusAluno.ATIVO),
                new AlunoResumoDTO(2L, "Lucas Souza", "lucasSouza@email.com", StatusAluno.ATIVO)
        );
        Page<AlunoResumoDTO> pageMock = new PageImpl<>(lista, pageable, lista.size());

        when(alunoService.buscarAlunosPorStatus(status, pageable)).thenReturn(pageMock);

        ResponseEntity<Page<AlunoResumoDTO>> response = alunoController.buscarAlunosPorStatus(status, pageable);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(pageMock, response.getBody())
        );
        verify(alunoService).buscarAlunosPorStatus(status, pageable);
    }

    @Test
    @DisplayName("Deve retornar todos os alunos paginados")
    void deveRetornarTodosOsAlunosPaginados() {

        Pageable pageable = PageRequest.of(0, 11, Sort.by("nome").ascending());

        List<AlunoResumoDTO> lista = List.of(
                new AlunoResumoDTO(1L, "Lucas Silva","lucasSouza@email.com", StatusAluno.ATIVO),
                new AlunoResumoDTO(2L, "Lucas Souza", "lucasSouza@email.com", StatusAluno.ATIVO)
        );
        Page<AlunoResumoDTO> pageMock = new PageImpl<>(lista, pageable, lista.size());

        when(alunoService.buscarTodosAlunos(pageable)).thenReturn(pageMock);

        ResponseEntity<Page<AlunoResumoDTO>> response = alunoController.buscarTodosAlunos(pageable);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(pageMock, response.getBody())
        );
        verify(alunoService).buscarTodosAlunos(pageable);
    }

    @Test
    @DisplayName("Deve retornar os detalhes completos do aluno por ID")
    void deveRetornarDetalhesDoAlunoPorId() {
        Long id = 1L;
        AlunoDetalhesDTO detalhesMock = new AlunoDetalhesDTO(
                id,
                "Lucas",
                "lucas@email.com",
                "(41)99960-2502",
                "9999999999",
                LocalDate.of(1998, 2, 25),
                StatusAluno.ATIVO,
                new DadosEnderecoDTO(
                        "Rua das Flores", "Centro", "12345678",
                        "100", "Apto 202", "Curitiba", "PR"
                )
        );

        when(alunoService.detalharAluno(id)).thenReturn(detalhesMock);

        ResponseEntity<AlunoDetalhesDTO> response = alunoController.detalharAluno(id);

        assertAll(
                () -> assertEquals(HttpStatus.OK, response.getStatusCode()),
                () -> assertEquals(detalhesMock, response.getBody())
        );
        verify(alunoService).detalharAluno(id);
    }

    @Test
    @DisplayName("Deve excluir um aluno e retornar status 204 No Content")
    void deveExcluirAlunoERetornarNoContent() {
        Long id = 1L;

        doNothing().when(alunoService).excluir(id);

        ResponseEntity<Void> response = alunoController.excluirAluno(id);

        assertAll(
                () -> assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode()),
                () -> assertNull(response.getBody())
        );
        verify(alunoService).excluir(id);
    }
}
