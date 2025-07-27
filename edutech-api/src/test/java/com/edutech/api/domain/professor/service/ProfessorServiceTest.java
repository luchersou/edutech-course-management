package com.edutech.api.domain.professor.service;

import com.edutech.api.domain.endereco.Endereco;
import com.edutech.api.domain.endereco.dto.DadosEnderecoDTO;
import com.edutech.api.domain.endereco.mapper.EnderecoMapper;
import com.edutech.api.domain.enums.Modalidade;
import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.professor.Professor;
import com.edutech.api.domain.professor.dto.ProfessorCreateDTO;
import com.edutech.api.domain.professor.dto.ProfessorDetalhesDTO;
import com.edutech.api.domain.professor.dto.ProfessorResumoDTO;
import com.edutech.api.domain.professor.dto.ProfessorUpdateDTO;
import com.edutech.api.domain.professor.enums.StatusProfessor;
import com.edutech.api.domain.professor.mapper.ProfessorMapper;
import com.edutech.api.domain.professor.repository.ProfessorRepository;
import com.edutech.api.domain.professor.validacoes.ValidadorCadastroProfessor;
import com.edutech.api.domain.turma.service.TurmaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Professor Service Testes")
class ProfessorServiceTest {

    @InjectMocks
    private ProfessorService professorService;
    @Mock
    private ProfessorMapper professorMapper;
    @Mock
    private EnderecoMapper enderecoMapper;
    @Mock
    private ProfessorRepository professorRepository;
    @Mock
    private List<ValidadorCadastroProfessor> validadores;

    @Test
    @DisplayName("Sucesso no cadastro: Deve permitir o registro de um novo professor com dados válidos")
    void deveCadastrarProfessorComSucesso() {
        var professorCreateDTO = new ProfessorCreateDTO(
                "João da Silva", "joao.silva@example.com", LocalDate.of(1980, 5, 15),
                "(11)98765-4321", "12345678901", Modalidade.PRESENCIAL,
                new DadosEnderecoDTO(
                        "Rua das Flores", "Centro", "12345678", "São Paulo",
                        "SP", "Apto 101", "100"));

        var endereco = new Endereco(
                "Rua das Flores", "Centro", "12345678",
                "São Paulo", "SP", "Apto 101", "100");

        var professorResumoDTO = new ProfessorResumoDTO(
                1L, "Ana Carolina Souza", "ana.souza@academia.com.br",
                Modalidade.EAD, StatusProfessor.ATIVO);

        when(enderecoMapper.toEndereco(professorCreateDTO.endereco())).thenReturn(endereco);
        when(professorRepository.save(any(Professor.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(professorMapper.toResumoDTO(any(Professor.class))).thenReturn(professorResumoDTO);

        var resultado = professorService.cadastrarProfessor(professorCreateDTO);

        ArgumentCaptor<Professor> professorCaptor = ArgumentCaptor.forClass(Professor.class);
        verify(professorRepository).save(professorCaptor.capture());
        var professorCapturado = professorCaptor.getValue();

        assertAll(
                // Retorno do método
                () -> assertNotNull(resultado),
                () -> assertEquals(professorResumoDTO, resultado),

                // Validação do objeto capturado
                () -> assertEquals("João da Silva", professorCapturado.getNome()),
                () -> assertEquals("joao.silva@example.com", professorCapturado.getEmail()),
                () -> assertEquals(LocalDate.of(1980, 5, 15), professorCapturado.getDataNascimento()),
                () -> assertEquals("(11)98765-4321", professorCapturado.getTelefone()),
                () -> assertEquals("12345678901", professorCapturado.getCpf()),
                () -> assertEquals(Modalidade.PRESENCIAL, professorCapturado.getModalidade()),
                () -> assertEquals(endereco, professorCapturado.getEndereco()),
                () -> assertEquals(StatusProfessor.ATIVO, professorCapturado.getStatus(), "Professor deve iniciar com status ATIVO")
        );

        verify(validadores).forEach(any());
        verify(enderecoMapper).toEndereco(professorCreateDTO.endereco());
        verify(professorMapper).toResumoDTO(professorCapturado);
    }

    @Test
    @DisplayName("Sucesso na atualização: Deve permitir a modificação dos dados de um professor existente com sucesso")
    void deveAtualizarProfessorComSucesso() {
        var professor = new Professor(
                "Ana Carolina Souza", "ana.souza@academia.com.br", LocalDate.of(1990, 3, 15),
                "(31)99876-5432", "12345678901", Modalidade.EAD,
                new Endereco(
                        "Rua das Flores", "Centro", "12345678",
                        "São Paulo", "SP", "Apto 101", "100"));

        var professorUpdateDTO = new ProfessorUpdateDTO(
                "Carlos Eduardo", "carlos.eduardo@email.com", LocalDate.of(1985, 8, 20),
                "(21)98765-1234", StatusProfessor.ATIVO, Modalidade.PRESENCIAL,
                new DadosEnderecoDTO(
                        "Rua das Flores", "Centro", "12345678",
                        "São Paulo", "SP", "Apto 101", "100"));

        var enderecoAtualizado = new Endereco(
                "Avenida Brasil", "Copacabana", "22021001",
                "Rio de Janeiro", "RJ", "Bloco B", "150"
        );

        var professorResumoDTO = new ProfessorResumoDTO(
                1L, "Ana Carolina Souza", "ana.souza@academia.com.br",
                Modalidade.EAD, StatusProfessor.ATIVO);

        when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
        when(enderecoMapper.toEndereco(professorUpdateDTO.endereco())).thenReturn(enderecoAtualizado);
        when(professorRepository.save(any(Professor.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(professorMapper.toResumoDTO(any(Professor.class))).thenReturn(professorResumoDTO);

        var resultado = professorService.atualizarProfessor(1L, professorUpdateDTO);

        ArgumentCaptor<Professor> professorCaptor = ArgumentCaptor.forClass(Professor.class);
        verify(professorRepository).save(professorCaptor.capture());
        var professorCapturado = professorCaptor.getValue();

        assertAll(
                // Retorno do método
                () -> assertNotNull(resultado),
                () -> assertEquals(professorResumoDTO, resultado),

                // Objeto capturado (depois da atualização)
                () -> assertEquals("Carlos Eduardo", professorCapturado.getNome()),
                () -> assertEquals("carlos.eduardo@email.com", professorCapturado.getEmail()),
                () -> assertEquals(LocalDate.of(1985, 8, 20), professorCapturado.getDataNascimento()),
                () -> assertEquals("(21)98765-1234", professorCapturado.getTelefone()),
                () -> assertEquals(StatusProfessor.ATIVO, professorCapturado.getStatus()),
                () -> assertEquals(Modalidade.PRESENCIAL, professorCapturado.getModalidade()),
                () -> assertEquals(enderecoAtualizado, professorCapturado.getEndereco())
        );

        verify(professorRepository).findById(1L);
        verify(enderecoMapper).toEndereco(professorUpdateDTO.endereco());
        verify(professorMapper).toResumoDTO(professorCapturado);
    }

    @Test
    @DisplayName("Falha na atualização: Deve lançar exceção ao tentar atualizar um professor que não está cadastrado")
    void deveLancarExcecaoAoAtualizarProfessorInexistente() {
        var professorUpdateDTO = new ProfessorUpdateDTO(
                "Carlos Eduardo", "carlos.eduardo@email.com", LocalDate.of(1985, 8, 20),
                "(21)98765-1234", StatusProfessor.ATIVO, Modalidade.PRESENCIAL,
                new DadosEnderecoDTO(
                        "Rua das Flores", "Centro", "12345678",
                        "São Paulo", "SP", "Apto 101", "100"));

        when(professorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ValidacaoException.class, () ->
                professorService.atualizarProfessor(99L, professorUpdateDTO));
    }

    @Test
    @DisplayName("Sucesso na busca por ID: Deve retornar um professor quando o ID fornecido for válido e encontrado")
    void deveBuscarProfessorPorIdComSucesso() {
        var professor = new Professor(
                "Ana Carolina Souza", "ana.souza@academia.com.br", LocalDate.of(1990, 3, 15),
                "(31)99876-5432", "12345678901", Modalidade.EAD,
                new Endereco(
                        "Rua das Flores", "Centro", "12345678",
                        "São Paulo", "SP", "Apto 101", "100"));

        var professorResumoDTO = new ProfessorResumoDTO(
                1L, "Ana Carolina Souza", "ana.souza@academia.com.br",
                Modalidade.EAD, StatusProfessor.ATIVO);

        when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
        when(professorMapper.toResumoDTO(professor)).thenReturn(professorResumoDTO);

        var resultado = professorService.buscarPorId(1L);

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(professorResumoDTO, resultado)
        );
    }

    @Test
    @DisplayName("Falha na busca por ID: Deve lançar exceção ao buscar um professor com um ID que não existe")
    void deveLancarExcecaoQuandoBuscarProfessorPorIdInexistente() {
        when(professorRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ValidacaoException.class, () ->
                professorService.buscarPorId(99L)
        );
    }

    @Test
    @DisplayName("Sucesso na busca por nome: Deve retornar professores quando a busca por nome for bem-sucedida")
    void deveBuscarProfessoresPorNomeComSucesso() {
        var professor = new Professor(
                "Ana Carolina Souza", "ana.souza@academia.com.br", LocalDate.of(1990, 3, 15),
                "(31)99876-5432", "12345678901", Modalidade.EAD,
                new Endereco(
                        "Rua das Flores", "Centro", "12345678",
                        "São Paulo", "SP", "Apto 101", "100"));

        var professorResumoDTO = new ProfessorResumoDTO(
                1L, "Ana Carolina Souza", "ana.souza@academia.com.br",
                Modalidade.EAD, StatusProfessor.ATIVO);

        when(professorRepository.findByNome("Maria")).thenReturn(List.of(professor));
        when(professorMapper.toResumoDTO(professor)).thenReturn(professorResumoDTO);

        var resultado = professorService.buscarProfessoresPorNome("Maria");

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(1, resultado.size()),
                () -> assertEquals(professorResumoDTO, resultado.getFirst())
        );
    }

    @Test
    @DisplayName("Falha na busca por nome: Deve lançar exceção quando nenhum professor for encontrado com o nome fornecido")
    void deveLancarExcecaoQuandoNaoEncontrarProfessorPorNome() {
        when(professorRepository.findByNome("Inexistente")).thenReturn(List.of());

        assertThrows(ValidacaoException.class, () ->
                professorService.buscarProfessoresPorNome("Inexistente")
        );
    }

    @Test
    @DisplayName("Sucesso na busca por modalidade: Deve retornar professores quando a busca por modalidade for bem-sucedida")
    void deveBuscarProfessoresPorModalidadeComSucesso() {
        var professor = new Professor(
                "Ana Carolina Souza", "ana.souza@academia.com.br", LocalDate.of(1990, 3, 15),
                "(31)99876-5432", "12345678901", Modalidade.EAD,
                new Endereco(
                        "Rua das Flores", "Centro", "12345678",
                        "São Paulo", "SP", "Apto 101", "100"));

        var professorResumoDTO = new ProfessorResumoDTO(
                1L, "Ana Carolina Souza", "ana.souza@academia.com.br",
                Modalidade.EAD, StatusProfessor.ATIVO);

        when(professorRepository.findByModalidade(Modalidade.PRESENCIAL)).thenReturn(List.of(professor));
        when(professorMapper.toResumoDTO(professor)).thenReturn(professorResumoDTO);

        var resultado = professorService.buscarProfessoresPorModalidade(Modalidade.PRESENCIAL);

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(1, resultado.size()),
                () -> assertEquals(professorResumoDTO, resultado.getFirst())
        );
    }

    @Test
    @DisplayName("Sucesso na busca geral: Deve retornar a lista completa de todos os professores cadastrados")
    void deveBuscarTodosProfessoresComSucesso() {
        var professor = new Professor(
                "Ana Carolina Souza", "ana.souza@academia.com.br", LocalDate.of(1990, 3, 15),
                "(31)99876-5432", "12345678901", Modalidade.EAD,
                new Endereco(
                        "Rua das Flores", "Centro", "12345678",
                        "São Paulo", "SP", "Apto 101", "100"));

        var professorResumoDTO = new ProfessorResumoDTO(
                1L, "Ana Carolina Souza", "ana.souza@academia.com.br",
                Modalidade.EAD, StatusProfessor.ATIVO);

        var pageable = PageRequest.of(0, 10);
        var page = new PageImpl<>(List.of(professor));

        when(professorRepository.findAll(pageable)).thenReturn(page);
        when(professorMapper.toResumoDTO(professor)).thenReturn(professorResumoDTO);

        var resultado = professorService.buscarTodosProfessores(pageable);

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(1, resultado.getTotalElements()),
                () -> assertEquals(professorResumoDTO, resultado.getContent().get(0))
        );
    }

    @Test
    @DisplayName("Sucesso ao detalhar: Deve exibir os detalhes completos de um professor com sucesso")
    void deveDetalharProfessorComSucesso() {
        var professor = new Professor(
                "Ana Carolina Souza", "ana.souza@academia.com.br", LocalDate.of(1990, 3, 15),
                "(31)99876-5432", "12345678901", Modalidade.EAD,
                new Endereco(
                        "Rua das Flores", "Centro", "12345678",
                        "São Paulo", "SP", "Apto 101", "100"));

        var professorDetalhesDTO = new ProfessorDetalhesDTO(
                1L, "Ana Carolina Souza", "ana.souza@academia.com.br",
                LocalDate.of(1990, 3, 15), "(31)99876-5432",
                Modalidade.EAD, StatusProfessor.ATIVO,
                new DadosEnderecoDTO(
                        "Rua das Flores", "Centro", "12345678",
                        "São Paulo", "SP", "Apto 101", "100"));

        when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
        when(professorMapper.toDetalhesDTO(professor)).thenReturn(professorDetalhesDTO);

        var resultado = professorService.detalharProfessor(1L);

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals("Ana Carolina Souza", resultado.nome()),
                () -> assertEquals("ana.souza@academia.com.br", resultado.email()),
                () -> assertEquals(Modalidade.EAD, resultado.modalidade()),
                () -> assertEquals(StatusProfessor.ATIVO, resultado.status()),
                () -> assertEquals("Rua das Flores", resultado.endereco().logradouro())
        );
    }

    @Test
    @DisplayName("Sucesso na exclusão: Deve permitir a exclusão lógica de um professor, alterando seu status para inativo")
    void deveExcluirProfessorComSucesso() {
        var professor = new Professor(
                "Ana Carolina Souza", "ana.souza@academia.com.br", LocalDate.of(1990, 3, 15),
                "(31)99876-5432", "12345678901", Modalidade.EAD,
                new Endereco(
                        "Rua das Flores", "Centro", "12345678",
                        "São Paulo", "SP", "Apto 101", "100"));

        when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));

        professorService.excluirProfessor(1L);

        verify(professorRepository).findById(1L);
        verify(professorRepository).save(professor);
        assertEquals(StatusProfessor.INATIVO, professor.getStatus());
    }

}
