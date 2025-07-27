package com.edutech.api.domain.aluno.service;

import com.edutech.api.domain.aluno.Aluno;
import com.edutech.api.domain.aluno.dto.AlunoCreateDTO;
import com.edutech.api.domain.aluno.dto.AlunoDetalhesDTO;
import com.edutech.api.domain.aluno.dto.AlunoResumoDTO;
import com.edutech.api.domain.aluno.dto.AlunoUpdateDTO;
import com.edutech.api.domain.aluno.enums.StatusAluno;
import com.edutech.api.domain.aluno.mapper.AlunoMapper;
import com.edutech.api.domain.aluno.repository.AlunoRepository;
import com.edutech.api.domain.aluno.validacoes.ValidadorCadastroAluno;
import com.edutech.api.domain.endereco.Endereco;
import com.edutech.api.domain.endereco.dto.DadosEnderecoDTO;
import com.edutech.api.domain.endereco.mapper.EnderecoMapper;
import com.edutech.api.domain.exception.ValidacaoException;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Aluno Service Testes")
class AlunoServiceTest {

    @InjectMocks
    private AlunoService alunoService;
    @Mock
    private AlunoRepository alunoRepository;
    @Mock
    private EnderecoMapper enderecoMapper;
    @Mock
    private List<ValidadorCadastroAluno> validadores;
    @Mock
    private AlunoMapper alunoMapper;

    @Test
    @DisplayName("Deve cadastrar aluno com sucesso")
    void deveCadastrarAlunoComSucesso() {
        var dadosEnderecoDTO = new DadosEnderecoDTO(
                "Rua A", "Centro", "12345678",
                "Cidade", "PR", "Ap 1", "11");

        var endereco = new Endereco(
                "Rua A", "Centro", "12345678",
                "Cidade", "PR", "Ap 1", "11");

        var alunoCreateDTO = new AlunoCreateDTO(
                "Lucas", "lucas@email.com", "999999999",
                "12345678900", LocalDate.of(2000, 1, 1),
                dadosEnderecoDTO);

        var alunoResumoDTO = new AlunoResumoDTO(
                1L, "Lucas", "lucas@email.com", StatusAluno.ATIVO);

        when(enderecoMapper.toEndereco(dadosEnderecoDTO)).thenReturn(endereco);
        when(alunoRepository.save(any(Aluno.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(alunoMapper.toResumoDTO(any(Aluno.class))).thenReturn(alunoResumoDTO);

        var resultado = alunoService.cadastrarAluno(alunoCreateDTO);

        assertEquals(alunoResumoDTO, resultado);

        ArgumentCaptor<Aluno> alunoCaptor = ArgumentCaptor.forClass(Aluno.class);
        verify(alunoRepository).save(alunoCaptor.capture());
        var alunoCapturado = alunoCaptor.getValue();

        assertEquals("Lucas", alunoCapturado.getNome());
        assertEquals("lucas@email.com", alunoCapturado.getEmail());
        assertEquals("12345678900", alunoCapturado.getCpf());
        assertEquals(StatusAluno.ATIVO, alunoCapturado.getStatus());
        assertEquals(endereco, alunoCapturado.getEndereco());

        verify(validadores).forEach(any());
        verify(alunoMapper).toResumoDTO(any(Aluno.class));
    }

    @Test
    @DisplayName("Deve atualizar aluno existente")
    void deveAtualizarAlunoComSucesso() {
        var endereco = new Endereco(
                "Rua A", "Centro", "12345678",
                "Cidade", "PR", "Ap 1", "11");

        var aluno = new Aluno(
                "Lucas", "lucas@email.com", "999999999",
                "12345678900", LocalDate.of(2000, 1, 1),
                endereco);

        var dadosEnderecoDTO = new DadosEnderecoDTO(
                "Rua A", "Centro", "12345678",
                "Cidade", "PR", "Ap 1", "11");

        var alunoResumoDTO = new AlunoResumoDTO(
                1L, "Lucas", "lucas@email.com", StatusAluno.ATIVO);

        var alunoUpdateDTO = new AlunoUpdateDTO(
                "Novo Nome", "novo@email.com", "888888888",
                StatusAluno.ATIVO, LocalDate.of(1995, 5, 5),
                dadosEnderecoDTO);

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(enderecoMapper.toEndereco(dadosEnderecoDTO)).thenReturn(endereco);
        when(alunoMapper.toResumoDTO(any(Aluno.class))).thenReturn(alunoResumoDTO);

        var resultado = alunoService.atualizarAluno(1L, alunoUpdateDTO);

        assertEquals(alunoResumoDTO, resultado);

        ArgumentCaptor<Aluno> alunoCaptor = ArgumentCaptor.forClass(Aluno.class);
        verify(alunoRepository).save(alunoCaptor.capture());
        var alunoCapturado = alunoCaptor.getValue();

        assertEquals("Novo Nome", alunoCapturado.getNome());
        assertEquals("novo@email.com", alunoCapturado.getEmail());
        assertEquals("888888888", alunoCapturado.getTelefone());
        assertEquals(StatusAluno.ATIVO, alunoCapturado.getStatus());
        assertEquals(LocalDate.of(1995, 5, 5), alunoCapturado.getDataDeNascimento());
        assertEquals(endereco, alunoCapturado.getEndereco());

        verify(alunoMapper).toResumoDTO(alunoCapturado);
    }

    @Test
    @DisplayName("Deve lançar exceção quando aluno não for encontrado")
    void deveBuscarAlunoPorIdComSucesso() {
        var endereco = new Endereco(
                "Rua A", "Centro", "12345678",
                "Cidade", "PR", "Ap 1", "11");

        var aluno = new Aluno(
                "Lucas","lucas@email.com","999999999",
                "12345678900", LocalDate.of(2000, 1, 1),
                endereco);

        var alunoResumoDTO = new AlunoResumoDTO(
                1L, "Lucas", "lucas@email.com", StatusAluno.ATIVO);

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(alunoMapper.toResumoDTO(aluno)).thenReturn(alunoResumoDTO);

        var resultado = alunoService.buscarAlunoPorId(1L);

        assertEquals(alunoResumoDTO, resultado);
        verify(alunoMapper).toResumoDTO(aluno);
    }

    @Test
    void deveLancarExcecaoQuandoAlunoNaoEncontrado() {
        when(alunoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ValidacaoException.class, () -> alunoService.buscarAlunoPorId(99L));
    }

    @Test
    void deveBuscarAlunoPorNomeComSucesso() {
        var endereco = new Endereco(
                "Rua A", "Centro", "12345678",
                "Cidade", "PR", "Ap 1", "11");

        var aluno = new Aluno(
                "Lucas","lucas@email.com","999999999",
                "12345678900", LocalDate.of(2000, 1, 1),
                endereco);

        var alunoResumoDTO = new AlunoResumoDTO(
                1L, "Lucas", "lucas@email.com", StatusAluno.ATIVO);

        var nome = "Lucas";
        var listaAlunos = List.of(aluno);

        when(alunoRepository.findByNome(nome)).thenReturn(listaAlunos);
        when(alunoMapper.toResumoDTO(aluno)).thenReturn(alunoResumoDTO);

        var resultado = alunoService.buscarAlunoPorNome(nome);

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(1, resultado.size()),
                () -> assertEquals(alunoResumoDTO, resultado.getFirst())
        );
    }

    @Test
    void deveLancarExcecaoQuandoNaoEncontrarAlunoPorNome() {
        var nome = "Inexistente";
        when(alunoRepository.findByNome(nome)).thenReturn(Collections.emptyList());

        assertThrows(ValidacaoException.class, () -> alunoService.buscarAlunoPorNome(nome));
    }

    @Test
    void deveBuscarAlunosPorStatusComSucesso() {
        var endereco = new Endereco(
                "Rua A", "Centro", "12345678",
                "Cidade", "PR", "Ap 1", "11");

        var aluno = new Aluno(
                "Lucas","lucas@email.com","999999999",
                "12345678900", LocalDate.of(2000, 1, 1),
                endereco);

        var alunoResumoDTO = new AlunoResumoDTO(
                1L, "Lucas", "lucas@email.com", StatusAluno.ATIVO);


        var pageable = PageRequest.of(0, 10);
        var alunos = new PageImpl<>(List.of(aluno));

        when(alunoRepository.findByStatus(StatusAluno.ATIVO, pageable)).thenReturn(alunos);
        when(alunoMapper.toResumoDTO(aluno)).thenReturn(alunoResumoDTO);

        var resultado = alunoService.buscarAlunosPorStatus(StatusAluno.ATIVO, pageable);

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(1, resultado.getTotalElements()),
                () -> assertEquals(alunoResumoDTO, resultado.getContent().getFirst())
        );
    }

    @Test
    void deveBuscarTodosAlunosComSucesso() {
        var endereco = new Endereco(
                "Rua A", "Centro", "12345678",
                "Cidade", "PR", "Ap 1", "11");

        var aluno = new Aluno(
                "Lucas","lucas@email.com","999999999",
                "12345678900", LocalDate.of(2000, 1, 1),
                endereco);

        var alunoResumoDTO = new AlunoResumoDTO(
                1L, "Lucas", "lucas@email.com", StatusAluno.ATIVO);

        var pageable = PageRequest.of(0, 10);
        var alunos = new PageImpl<>(List.of(aluno));

        when(alunoRepository.findAll(pageable)).thenReturn(alunos);
        when(alunoMapper.toResumoDTO(aluno)).thenReturn(alunoResumoDTO);

        var resultado = alunoService.buscarTodosAlunos(pageable);

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(1, resultado.getTotalElements()),
                () -> assertEquals(alunoResumoDTO, resultado.getContent().getFirst())
        );
    }

    @Test
    void deveDetalharAlunoComSucesso() {
        var endereco = new Endereco(
                "Rua A", "Centro", "12345678",
                "Cidade", "PR", "Ap 1", "11");

        var dadosEnderecoDTO = new DadosEnderecoDTO(
                "Rua A", "Centro", "12345678",
                "Cidade", "PR", "Ap 1", "11");

        var aluno = new Aluno(
                "Lucas","lucas@email.com","999999999",
                "12345678900", LocalDate.of(2000, 1, 1),
                endereco);

        var alunoDetalhesDTO = new AlunoDetalhesDTO(
                1L,"Lucas","lucas@email.com","999999999","12345678900",
                LocalDate.of(2000, 1, 1),StatusAluno.ATIVO,
                dadosEnderecoDTO);

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(alunoMapper.toDetalhesDTO(aluno)).thenReturn(alunoDetalhesDTO);

        var resultado = alunoService.detalharAluno(1L);

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(1L, resultado.id()),
                () -> assertEquals("Lucas", resultado.nome()),
                () -> assertEquals("lucas@email.com", resultado.email()),
                () -> assertEquals("999999999", resultado.telefone()),
                () -> assertEquals("12345678900", resultado.cpf()),
                () -> assertEquals(LocalDate.of(2000, 1, 1), resultado.dataDeNascimento()),
                () -> assertEquals(StatusAluno.ATIVO, resultado.status()),

                () -> assertNotNull(resultado.endereco()),
                () -> assertEquals("Rua A", resultado.endereco().logradouro()),
                () -> assertEquals("Centro", resultado.endereco().bairro()),
                () -> assertEquals("12345678", resultado.endereco().cep())
        );
    }

    @Test
    void deveExcluirAlunoComSucesso() {
        var endereco = new Endereco(
                "Rua A", "Centro", "12345678",
                "Cidade", "PR", "Ap 1", "11");

        var aluno = new Aluno(
                "Lucas","lucas@email.com","999999999",
                "12345678900", LocalDate.of(2000, 1, 1),
                endereco);

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));

        alunoService.excluir(1L);

        assertAll(
                () -> verify(alunoRepository).findById(1L),
                () -> verify(alunoRepository).save(aluno),
                () -> assertEquals(StatusAluno.INATIVO, aluno.getStatus())
        );
    }

    @Test
    void deveLancarExcecaoAoExcluirAlunoInexistente() {
        when(alunoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ValidacaoException.class, () -> alunoService.excluir(99L));
    }
}
