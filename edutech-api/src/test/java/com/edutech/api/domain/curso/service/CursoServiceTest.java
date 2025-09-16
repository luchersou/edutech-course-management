package com.edutech.api.domain.curso.service;

import com.edutech.api.domain.curso.Curso;
import com.edutech.api.domain.curso.dto.CursoCreateDTO;
import com.edutech.api.domain.curso.dto.CursoDetalhesDTO;
import com.edutech.api.domain.curso.dto.CursoResumoDTO;
import com.edutech.api.domain.curso.dto.CursoUpdateDTO;
import com.edutech.api.domain.curso.enums.CategoriaCurso;
import com.edutech.api.domain.curso.enums.NivelCurso;
import com.edutech.api.domain.curso.enums.StatusCurso;
import com.edutech.api.domain.curso.mapper.CursoMapper;
import com.edutech.api.domain.curso.repository.CursoRepository;
import com.edutech.api.domain.endereco.Endereco;
import com.edutech.api.domain.enums.Modalidade;
import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.professor.Professor;
import com.edutech.api.domain.professor.enums.StatusProfessor;
import com.edutech.api.domain.professor.repository.ProfessorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Curso Service Testes")
class CursoServiceTest {

    @Mock
    private CursoRepository cursoRepository;
    @Mock
    private ProfessorRepository professorRepository;
    @Mock
    private CursoMapper cursoMapper;
    @InjectMocks
    private CursoService cursoService;

    @Test
    @DisplayName("Sucesso no cadastro: Deve permitir o registro de um novo curso com dados válidos")
    void deveCadastrarCursoComSucesso() {
        var curso = new Curso(
                "Java Fundamentals", "Curso básico de Java", 40,
                2, NivelCurso.BASICO, CategoriaCurso.PROGRAMACAO);

        var cursoResumoDTO = new CursoResumoDTO(
                1L, "Java Fundamentals", StatusCurso.ATIVO,
                40, NivelCurso.BASICO, CategoriaCurso.PROGRAMACAO);

        var cursoCreateDTO = new CursoCreateDTO(
                "Java Fundamentals", "Curso básico de Java", 40,
                2, NivelCurso.BASICO, CategoriaCurso.PROGRAMACAO);

        when(cursoRepository.save(any(Curso.class))).thenReturn(curso);
        when(cursoMapper.toResumoDTO(any(Curso.class))).thenReturn(cursoResumoDTO);

        var resultado = cursoService.cadastrarCurso(cursoCreateDTO);

        ArgumentCaptor<Curso> cursoCaptor = ArgumentCaptor.forClass(Curso.class);
        verify(cursoRepository).save(cursoCaptor.capture());
        var cursoCapturado = cursoCaptor.getValue();

        assertAll("Validações do resultado retornado",
                () -> assertNotNull(resultado),
                () -> assertEquals(1L, resultado.id()),
                () -> assertEquals("Java Fundamentals", resultado.nome()),
                () -> assertEquals(NivelCurso.BASICO, resultado.nivel()),
                () -> assertEquals(CategoriaCurso.PROGRAMACAO, resultado.categoria())
        );

        assertAll("Validações do curso salvo no repositório",
                () -> assertEquals("Java Fundamentals", cursoCapturado.getNome()),
                () -> assertEquals("Curso básico de Java", cursoCapturado.getDescricao()),
                () -> assertEquals(40, cursoCapturado.getCargaHorariaTotal()),
                () -> assertEquals(2, cursoCapturado.getDuracaoMeses()),
                () -> assertEquals(NivelCurso.BASICO, cursoCapturado.getNivel()),
                () -> assertEquals(CategoriaCurso.PROGRAMACAO, cursoCapturado.getCategoria()),
                () -> assertEquals(StatusCurso.ATIVO, cursoCapturado.getStatus())
        );

        verify(cursoMapper).toResumoDTO(cursoCapturado);
    }

    @Test
    @DisplayName("Sucesso na atualização: Deve permitir a modificação dos dados de um curso existente com sucesso")
    void deveAtualizarCursoComSucesso() {
        var curso = new Curso(
                "Java Fundamentals", "Curso java", 40,
                3, NivelCurso.BASICO, CategoriaCurso.PROGRAMACAO);

        var cursoUpdateDTO = new CursoUpdateDTO(
                "Java Advanced", "Curso avançado de Java", 60,
                3, NivelCurso.AVANCADO, CategoriaCurso.PROGRAMACAO);

        var cursoResumoDTO = new CursoResumoDTO(
                1L, "Java Advanced", StatusCurso.ATIVO,
                60, NivelCurso.AVANCADO, CategoriaCurso.PROGRAMACAO);

        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(cursoRepository.save(any(Curso.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(cursoMapper.toResumoDTO(any(Curso.class))).thenReturn(cursoResumoDTO);

        var resultado = cursoService.atualizarCurso(1L, cursoUpdateDTO);

        ArgumentCaptor<Curso> cursoCaptor = ArgumentCaptor.forClass(Curso.class);
        verify(cursoRepository).save(cursoCaptor.capture());
        var cursoCapturado = cursoCaptor.getValue();

        assertAll("Verificar DTO retornado",
                () -> assertNotNull(resultado),
                () -> assertEquals(1L, resultado.id()),
                () -> assertEquals("Java Advanced", resultado.nome()),
                () -> assertEquals(StatusCurso.ATIVO, resultado.status())
        );

        assertAll("Verificar entidade persistida",
                () -> assertEquals("Java Advanced", cursoCapturado.getNome()),
                () -> assertEquals("Curso avançado de Java", cursoCapturado.getDescricao()),
                () -> assertEquals(60, cursoCapturado.getCargaHorariaTotal()),
                () -> assertEquals(3, cursoCapturado.getDuracaoMeses()),
                () -> assertEquals(NivelCurso.AVANCADO, cursoCapturado.getNivel()),
                () -> assertEquals(CategoriaCurso.PROGRAMACAO, cursoCapturado.getCategoria()),
                () -> assertEquals(StatusCurso.ATIVO, cursoCapturado.getStatus())
        );

        verify(cursoRepository).findById(1L);
        verify(cursoMapper).toResumoDTO(cursoCapturado);
    }

    @Test
    @DisplayName("Sucesso na busca por ID: Deve retornar um curso quando o ID fornecido for válido e encontrado")
    void deveBuscarCursoPorIdComSucesso() {
        var curso = new Curso(
                "Java Fundamentals","Curso java",40,
                3, NivelCurso.BASICO, CategoriaCurso.PROGRAMACAO);

        var cursoResumoDTO = new CursoResumoDTO(
                1L,"Java Fundamentals",StatusCurso.ATIVO,
                40, NivelCurso.BASICO, CategoriaCurso.PROGRAMACAO);

        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(cursoMapper.toResumoDTO(curso)).thenReturn(cursoResumoDTO);

        var resultado = cursoService.buscarPorId(1L);

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(1L, resultado.id()),
                () -> assertEquals("Java Fundamentals", resultado.nome())
        );

        verify(cursoRepository).findById(1L);
        verify(cursoMapper).toResumoDTO(curso);
    }

    @Test
    @DisplayName("Sucesso ao detalhar: Deve exibir os detalhes completos de um curso com sucesso ao buscar por ID")
    void deveDetalharCursoPorIdComSucesso() {
        var curso = new Curso(
                "Java Fundamentals","Curso java",40,
                3, NivelCurso.BASICO, CategoriaCurso.PROGRAMACAO);

        var cursoDetalhesDTO = new CursoDetalhesDTO(
                1L, "Java Fundamentals", "Curso básico de Java", 40,
                2, StatusCurso.ATIVO, NivelCurso.BASICO, CategoriaCurso.PROGRAMACAO,
                List.of("João Silva", "Maria Santos"));

        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(cursoMapper.toDetalhesDTO(curso)).thenReturn(cursoDetalhesDTO);

        var resultado = cursoService.detalharPorId(1L);

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals("Curso básico de Java", resultado.descricao())
        );

        verify(cursoRepository).findById(1L);
        verify(cursoMapper).toDetalhesDTO(curso);
    }

    @Test
    @DisplayName("Sucesso na busca paginada: Deve retornar todos os cursos, aplicando a paginação corretamente")
    void deveBuscarTodosOsCursosComPaginacao() {
        var curso = new Curso(
                "Java Fundamentals","Curso java",40,
                3, NivelCurso.BASICO, CategoriaCurso.PROGRAMACAO);

        var cursoResumoDTO = new CursoResumoDTO(
                1L,"Java Fundamentals",StatusCurso.ATIVO,
                40, NivelCurso.BASICO, CategoriaCurso.PROGRAMACAO);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Curso> page = new PageImpl<>(List.of(curso));

        when(cursoRepository.findAll(pageable)).thenReturn(page);
        when(cursoMapper.toResumoDTO(curso)).thenReturn(cursoResumoDTO);

        var resultado = cursoService.buscarTodosCursos(pageable);

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(1, resultado.getContent().size()),
                () -> assertEquals("Java Fundamentals", resultado.getContent().get(0).nome())
        );

        verify(cursoRepository).findAll(pageable);
        verify(cursoMapper).toResumoDTO(curso);
    }

    @Test
    @DisplayName("Sucesso na busca por carga horária: Deve retornar cursos cuja carga horária esteja dentro do intervalo especificado")
    void deveBuscarPorCargaHorariaDentroDoIntervalo() {
        var curso = new Curso(
                "Java Fundamentals","Curso java",40,
                3, NivelCurso.BASICO, CategoriaCurso.PROGRAMACAO);

        var cursoResumoDTO = new CursoResumoDTO(
                1L,"Java Fundamentals",StatusCurso.ATIVO,
                40, NivelCurso.BASICO, CategoriaCurso.PROGRAMACAO);

        when(cursoRepository.findByCargaHorariaTotalBetween(30, 50)).thenReturn(List.of(curso));
        when(cursoMapper.toResumoDTO(curso)).thenReturn(cursoResumoDTO);

        var resultado = cursoService.buscarPorCargaHorariaIntervalo(30, 50);

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(1, resultado.size()),
                () -> assertEquals("Java Fundamentals", resultado.get(0).nome())
        );

        verify(cursoRepository).findByCargaHorariaTotalBetween(30, 50);
        verify(cursoMapper).toResumoDTO(curso);
    }

    @Test
    @DisplayName("Sucesso na busca por nível: Deve retornar cursos quando a busca por nível for bem-sucedida")
    void deveBuscarCursosPorNivelComSucesso() {
        var curso = new Curso(
                "Java Fundamentals","Curso java",40,
                3, NivelCurso.BASICO, CategoriaCurso.PROGRAMACAO);

        var cursoResumoDTO = new CursoResumoDTO(
                1L,"Java Fundamentals",StatusCurso.ATIVO,
                40, NivelCurso.BASICO, CategoriaCurso.PROGRAMACAO);

        when(cursoRepository.findByNivel(NivelCurso.BASICO)).thenReturn(List.of(curso));
        when(cursoMapper.toResumoDTO(curso)).thenReturn(cursoResumoDTO);

        var resultado = cursoService.buscarPorNivel(NivelCurso.BASICO);

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(1, resultado.size()),
                () -> assertEquals("Java Fundamentals", resultado.get(0).nome())
        );

        verify(cursoRepository).findByNivel(NivelCurso.BASICO);
        verify(cursoMapper).toResumoDTO(curso);
    }

    @Test
    @DisplayName("Falha na busca por nível: Deve lançar exceção ao tentar buscar cursos com um nível nulo")
    void deveLancarExcecaoQuandoNivelForNulo() {
        var exception = assertThrows(ValidacaoException.class, () -> cursoService.buscarPorNivel(null));

        assertEquals("Nivel do curso deve ser informado", exception.getMessage());
    }

    @Test
    @DisplayName("Sucesso na busca por nome: Deve retornar cursos quando a busca por nome for bem-sucedida")
    void deveBuscarCursoPorNomeComSucesso() {
        var curso = new Curso(
                "Java Fundamentals","Curso java",40,
                3, NivelCurso.BASICO, CategoriaCurso.PROGRAMACAO);

        var cursoResumoDTO = new CursoResumoDTO(
                1L,"Java Fundamentals",StatusCurso.ATIVO,
                40, NivelCurso.BASICO, CategoriaCurso.PROGRAMACAO);

        when(cursoRepository.findByNome("Java Fundamentals")).thenReturn(Optional.of(curso));
        when(cursoMapper.toResumoDTO(curso)).thenReturn(cursoResumoDTO);

        var resultado = cursoService.buscarPorNome("Java Fundamentals");

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals("Java Fundamentals", resultado.nome())
        );

        verify(cursoRepository).findByNome("Java Fundamentals");
        verify(cursoMapper).toResumoDTO(curso);
    }

    @Test
    @DisplayName("Falha na busca por nome: Deve lançar exceção quando nenhum curso for encontrado com o nome fornecido")
    void deveLancarExcecaoQuandoCursoNaoForEncontradoPorNome() {
        when(cursoRepository.findByNome("Python")).thenReturn(Optional.empty());

        var exception = assertThrows(ValidacaoException.class, () -> cursoService.buscarPorNome("Python"));

        assertEquals("Curso com nome 'Python' não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Sucesso na ativação: Deve permitir a ativação de um curso, alterando seu status para ativo")
    void deveAtivarCursoComSucesso() {
        var curso = new Curso(
                "Java para todos", "Curso completo de java", 100,
                3, NivelCurso.AVANCADO, CategoriaCurso.PROGRAMACAO);

        ReflectionTestUtils.setField(curso, "status", StatusCurso.INATIVO);
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));

        cursoService.ativarCurso(1L);

        assertEquals(StatusCurso.ATIVO, curso.getStatus());
        verify(cursoRepository).save(curso);
    }

    @Test
    @DisplayName("Sucesso na inativação: Deve permitir a inativação de um curso, alterando seu status para inativo")
    void deveInativarCursoComSucesso() {
        var curso = new Curso(
                "Java para todos", "Curso completo de java", 100,
                3, NivelCurso.AVANCADO, CategoriaCurso.PROGRAMACAO);

        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));

        cursoService.inativarCurso(1L);

        assertEquals(StatusCurso.INATIVO, curso.getStatus());
        verify(cursoRepository).save(curso);
    }

    /**
     * Vincular/Desvincular e listar professor de curso
     */
    @Test
    @DisplayName("Sucesso na vinculação: Deve permitir vincular um professor a um curso ou disciplina com sucesso")
    void deveVincularProfessorComSucesso() {
        var endereco = new Endereco(
                "Rua A", "Centro", "12345678",
                "Cidade", "PR", "Ap 1", "11");

        var curso = new Curso(
                "Java para todos", "Curso completo de java", 100,
                3, NivelCurso.AVANCADO, CategoriaCurso.PROGRAMACAO);

        var professor = new Professor("João da Silva", "joao.silva@example.com", LocalDate.of(1985, 4, 15),
                "(11) 91234-5678", "123.456.789-00", Modalidade.PRESENCIAL,
                endereco);

        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(professorRepository.findById(2L)).thenReturn(Optional.of(professor));

        cursoService.vincularProfessor(1L, 2L);

        assertTrue(curso.getProfessores().contains(professor));
        verify(cursoRepository).save(curso);
    }

    @Test
    @DisplayName("Sucesso na desvinculação: Deve permitir desvincular um professor de um curso ou disciplina com sucesso")
    void deveDesvincularProfessorComSucesso() {
        var endereco = new Endereco(
                "Rua A", "Centro", "12345678",
                "Cidade", "PR", "Ap 1", "11");

        var curso = new Curso(
                "Java para todos", "Curso completo de java", 100,
                3, NivelCurso.AVANCADO, CategoriaCurso.PROGRAMACAO);

        var professor = new Professor("João da Silva", "joao.silva@example.com", LocalDate.of(1985, 4, 15),
                "(11) 91234-5678", "123.456.789-00", Modalidade.PRESENCIAL,
                endereco);

        curso.getProfessores().add(professor);

        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(professorRepository.findById(2L)).thenReturn(Optional.of(professor));

        cursoService.desvincularProfessor(1L, 2L);

        assertAll(
                () -> assertFalse(curso.getProfessores().contains(professor)),
                () -> assertEquals(0, curso.getProfessores().size())
        );
        verify(cursoRepository).save(curso);
    }

    @Test
    @DisplayName("Falha na operação: Deve lançar exceção ao tentar vincular ou desvincular um professor inativo")
    void deveLancarExcecaoQuandoProfessorNaoEstiverAtivo() {
        var professor = mock(Professor.class);

        var curso = new Curso(
                "Java para todos", "Curso completo de java", 100,
                3, NivelCurso.AVANCADO, CategoriaCurso.PROGRAMACAO);

        when(professor.getStatus()).thenReturn(StatusProfessor.INATIVO);

        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));

        var exception = assertThrows(ValidacaoException.class, () -> cursoService.vincularProfessor(1L, 1L));

        assertEquals("Não é possível vincular um professor com status diferente de ATIVO ao curso.", exception.getMessage());
    }

    @Test
    @DisplayName("Falha na desvinculação: Deve lançar exceção ao tentar desvincular um professor que não está associado ao curso")
    void deveLancarExcecaoQuandoProfessorNaoEstiverVinculadoAoCurso() {
        var endereco = new Endereco(
                "Rua A", "Centro", "12345678",
                "Cidade", "PR", "Ap 1", "11");

        var curso = new Curso(
                "Java para todos", "Curso completo de java", 100,
                3, NivelCurso.AVANCADO, CategoriaCurso.PROGRAMACAO);

        var professor = new Professor("João da Silva", "joao.silva@example.com", LocalDate.of(1985, 4, 15),
                "(11) 91234-5678", "123.456.789-00", Modalidade.PRESENCIAL,
                endereco);

        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));
        when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));

        var exception = assertThrows(ValidacaoException.class, () -> cursoService.desvincularProfessor(1L, 1L));

        assertEquals("Este professor não esta vinculado ao curso", exception.getMessage());
    }

    @Test
    @DisplayName("Sucesso na listagem: Deve retornar a lista de todos os cursos que um professor está vinculado")
    void deveListarCursosDoProfessorComSucesso() {
        var endereco = new Endereco(
                "Rua A", "Centro", "12345678",
                "Cidade", "PR", "Ap 1", "11");

        var professor = new Professor("João da Silva", "joao.silva@example.com", LocalDate.of(1985, 4, 15),
                "(11) 91234-5678", "123.456.789-00", Modalidade.PRESENCIAL,
                endereco);

        var curso = new Curso(
                "Java Fundamentals","Curso java",40,
                3, NivelCurso.BASICO, CategoriaCurso.PROGRAMACAO);

        var cursoResumoDTO = new CursoResumoDTO(
                1L,"Java Fundamentals",StatusCurso.ATIVO,
                40, NivelCurso.BASICO, CategoriaCurso.PROGRAMACAO);

        when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
        when(cursoRepository.findByProfessoresId(1L)).thenReturn(List.of(curso));
        when(cursoMapper.toResumoDTO(curso)).thenReturn(cursoResumoDTO);

        var resultado = cursoService.listarCursosDoProfessor(1L);

        assertAll(
                () -> assertNotNull(resultado),
                () -> assertEquals(1, resultado.size()),
                () -> assertEquals("Java Fundamentals", resultado.get(0).nome())
        );

        verify(cursoRepository).findByProfessoresId(1L);
        verify(cursoMapper).toResumoDTO(curso);
    }

}
