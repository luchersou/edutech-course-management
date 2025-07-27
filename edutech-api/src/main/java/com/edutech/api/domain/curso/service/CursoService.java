package com.edutech.api.domain.curso.service;

import com.edutech.api.domain.aluno.Aluno;
import com.edutech.api.domain.aluno.dto.AlunoResumoDTO;
import com.edutech.api.domain.curso.Curso;
import com.edutech.api.domain.curso.dto.CursoCreateDTO;
import com.edutech.api.domain.curso.dto.CursoDetalhesDTO;
import com.edutech.api.domain.curso.dto.CursoResumoDTO;
import com.edutech.api.domain.curso.dto.CursoUpdateDTO;
import com.edutech.api.domain.curso.enums.NivelCurso;
import com.edutech.api.domain.curso.mapper.CursoMapper;
import com.edutech.api.domain.curso.repository.CursoRepository;
import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.professor.Professor;
import com.edutech.api.domain.professor.enums.StatusProfessor;
import com.edutech.api.domain.professor.repository.ProfessorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository cursoRepository;
    private final ProfessorRepository professorRepository;
    private final CursoMapper cursoMapper;

    @Transactional
    public CursoResumoDTO cadastrarCurso(CursoCreateDTO dto){

        var curso = new Curso(
                dto.nome(),
                dto.descricao(),
                dto.cargaHorariaTotal(),
                dto.duracaoMeses(),
                dto.nivel(),
                dto.categoria()
        );

        cursoRepository.save(curso);
        return cursoMapper.toResumoDTO(curso);
    }

    public CursoResumoDTO atualizarCurso(Long id, CursoUpdateDTO dto){
        var curso = buscarCursoPorId(id);

        curso.atualizar(
                dto.nome(),
                dto.descricao(),
                dto.cargaHorariaTotal(),
                dto.duracaoMeses(),
                dto.nivel(),
                dto.categoria()
        );
        cursoRepository.save(curso);
        return cursoMapper.toResumoDTO(curso);
    }

    public CursoResumoDTO buscarPorId(Long id){
        var curso = buscarCursoPorId(id);
        return cursoMapper.toResumoDTO(curso);
    }

    public CursoDetalhesDTO detalharPorId(Long id){
        var curso = buscarCursoPorId(id);
        return cursoMapper.toDetalhesDTO(curso);
    }

    public Page<CursoResumoDTO> buscarTodosCursos(Pageable pageable){
        Page<Curso> Cursos = cursoRepository.findAll(pageable);
        return Cursos.map(cursoMapper::toResumoDTO);
    }

    public List<CursoResumoDTO> buscarPorCargaHorariaIntervalo(Integer cargaHorariaMin, Integer cargaHorariaMax) {
        validaCargaHoraria(cargaHorariaMin, cargaHorariaMax);

        List<Curso> cursos = cursoRepository.findByCargaHorariaTotalBetween(cargaHorariaMin, cargaHorariaMax);

        return cursos.stream()
                .map(cursoMapper::toResumoDTO)
                .collect(Collectors.toList());
    }

    public List<CursoResumoDTO> buscarPorNivel(NivelCurso nivel) {
        if(nivel == null) {
            throw new ValidacaoException("Nivel do curso deve ser informado");
        }

        List<Curso> cursos = cursoRepository.findByNivel(nivel);

        return cursos.stream()
                .map(cursoMapper::toResumoDTO)
                .collect(Collectors.toList());
    }

    public CursoResumoDTO buscarPorNome(String nome) {
        if(nome == null || nome.trim().isEmpty()) {
            throw new ValidacaoException("Nome do curso deve ser informado");
        }

        Optional<Curso> curso = cursoRepository.findByNome(nome.trim());

        return curso.map(cursoMapper::toResumoDTO)
                .orElseThrow(() -> new ValidacaoException("Curso com nome '" + nome + "' não encontrado"));
    }

    @Transactional
    public void ativarCurso(Long id) {
        var curso = buscarCursoPorId(id);
        curso.ativar();
        cursoRepository.save(curso);
    }

    @Transactional
    public void inativarCurso(Long id) {
        var curso = buscarCursoPorId(id);
        curso.inativar();
        cursoRepository.save(curso);
    }

    /**
     * Vincular/Desvincular e listar professor de curso
     */

    @Transactional
    public void vincularProfessor(Long cursoId, Long professorId) {
        var curso = buscarCursoPorId(cursoId);
        var professor = buscarProfessorPorId(professorId);

        if (professor.getStatus() != StatusProfessor.ATIVO) {
            throw new ValidacaoException("Não é possível vincular um professor com status diferente de ATIVO ao curso.");
        }

        if (curso.getProfessores().contains(professor)) {
        throw new ValidacaoException("Este professor já está vinculado ao curso");
        }

        curso.getProfessores().add(professor);
        cursoRepository.save(curso);
    }

    @Transactional
    public void desvincularProfessor(Long cursoId, Long professorId) {
        var curso = buscarCursoPorId(cursoId);
        var professor = buscarProfessorPorId(professorId);

        if (!curso.getProfessores().contains(professor)) {
            throw new ValidacaoException("Este professor não esta vinculado ao curso");
        }

        curso.getProfessores().remove(professor);
        cursoRepository.save(curso);
    }

    public List<CursoResumoDTO> listarCursosDoProfessor(Long professorId) {
        buscarProfessorPorId(professorId);

        List<Curso> cursos = cursoRepository.findByProfessoresId(professorId);
        return cursos.stream()
                .map(cursoMapper::toResumoDTO)
                .collect(Collectors.toList());
    }

    /**
     * Auxiliares
     */
    private void validaCargaHoraria(Integer cargaHorariaMin, Integer cargaHorariaMax){
        if (cargaHorariaMin == null && cargaHorariaMax == null) {
            throw new ValidacaoException("Pelo menos um parâmetro deve ser informado");
        }

        if (cargaHorariaMin != null && cargaHorariaMin <= 0) {
            throw new ValidacaoException("Carga horaria minima deve ser positiva");
        }

        if (cargaHorariaMax != null && cargaHorariaMax <= 0) {
            throw new ValidacaoException("Carga horaria maxima deve ser positiva");
        }

        if (cargaHorariaMin != null && cargaHorariaMax != null && cargaHorariaMin > cargaHorariaMax) {
            throw new ValidacaoException("Carga horaria minima não pode ser maior que a maxima");
        }
    }

    private Professor buscarProfessorPorId(Long id){
        return professorRepository.findById(id)
                .orElseThrow(() -> new ValidacaoException("professor com ID " + id + " não encontrado"));
    }

    private Curso buscarCursoPorId(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new ValidacaoException("Curso com ID " + id + " não encontrado"));
    }
}
