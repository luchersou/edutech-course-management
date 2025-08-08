package com.edutech.api.domain.turma.service;

import com.edutech.api.domain.curso.Curso;
import com.edutech.api.domain.curso.repository.CursoRepository;
import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.professor.Professor;
import com.edutech.api.domain.professor.repository.ProfessorRepository;
import com.edutech.api.domain.turma.Turma;
import com.edutech.api.domain.turma.dto.*;
import com.edutech.api.domain.turma.mapper.TurmaMapper;
import com.edutech.api.domain.turma.repository.TurmaRepository;
import com.edutech.api.domain.turma.validacoes.atualiza_turma.ValidadorAtualizaTurma;
import com.edutech.api.domain.turma.validacoes.cadastra_turma.ValidadorCadastroTurma;
import com.edutech.api.domain.turma.validacoes.desvincula_curso.ValidadorDesvinculoCurso;
import com.edutech.api.domain.turma.validacoes.desvincula_professor.ValidadorDesvinculoProfessor;
import com.edutech.api.domain.turma.validacoes.inicia_turma.ValidadorIniciarTurma;
import com.edutech.api.domain.turma.validacoes.vincula_curso.ValidadorVinculoCurso;
import com.edutech.api.domain.turma.validacoes.vincula_professor.ValidadorVinculoProfessor;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TurmaService {

    private final TurmaRepository turmaRepository;
    private final ProfessorRepository professorRepository;
    private final CursoRepository cursoRepository;
    private final TurmaMapper turmaMapper;
    private final List<ValidadorCadastroTurma> validadoresCadastroTurma;
    private final List<ValidadorAtualizaTurma> validadoresAtualizaTurma;
    private final List<ValidadorVinculoProfessor> validadoresVinculoProfessor;
    private final List<ValidadorDesvinculoProfessor> validadoresDesvinculoProfessor;
    private final List<ValidadorVinculoCurso> validadoresVinculoCurso;
    private final List<ValidadorDesvinculoCurso> validadoresDesvinculoCurso;
    private final List<ValidadorIniciarTurma> validadorIniciaTurmas;

    @Transactional
    public TurmaResumoDTO cadastrarTurma(TurmaCreateDTO dto){
        validadoresCadastroTurma.forEach(validador -> validador.validar(dto));

        var turma = criarTurma(dto);

        turmaRepository.save(turma);
        return turmaMapper.toResumoDTO(turma);
    }

    @Transactional
    public TurmaResumoDTO atualizarTurma(Long turmaId, TurmaUpdateDTO dto) {
        var turmaAtual = buscarTurmaPorId(turmaId);

        validadoresAtualizaTurma.forEach(validador -> validador.validar(turmaAtual, dto));

        turmaAtual.atualizar(
                dto.codigo(),
                dto.dataInicio(),
                dto.dataFim(),
                dto.horarioInicio(),
                dto.horarioFim(),
                dto.vagasTotais(),
                dto.modalidade()
        );

        var turmaSalva = turmaRepository.save(turmaAtual);
        return turmaMapper.toResumoDTO(turmaSalva);
    }

    public TurmaDetalhesDTO detalharPorId(Long id) {
        var turma = buscarTurmaPorId(id);

        return turmaMapper.toDetalhesDTO(turma);
    }

    public TurmaResumoDTO buscarPorCodigo(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new ValidacaoException("Código da turma é obrigatório.");
        }

        var turma = turmaRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ValidacaoException("Turma não encontrada com o código: " + codigo));

        return turmaMapper.toResumoDTO(turma);
    }

    public Page<TurmaResumoDTO> buscarTodasTurmas(Pageable pageable){
        Page<Turma> turmas = turmaRepository.findAll(pageable);
        return turmas.map(turmaMapper::toResumoDTO);
    }

    public TurmaComMatriculasDTO buscarTurmaComMatriculas(Long turmaId) {
        var turma = buscarTurmaPorId(turmaId);
        return turmaMapper.toTurmaComMatriculasDTO(turma);
    }

    public void iniciarTurma(Long turmaId) {
        var turma = buscarTurmaPorId(turmaId);

        validadorIniciaTurmas.forEach(validador -> validador.validar(turma));

        turma.iniciar();
        turmaRepository.save(turma);
    }

    public void concluirTurma(Long turmaId) {
        var turma = buscarTurmaPorId(turmaId);
        turma.concluir();
        turmaRepository.save(turma);
    }

    @Transactional
    public void cancelarTurma(Long turmaId){
        var turma = buscarTurmaPorId(turmaId);

        turma.cancelar();
    }

    /**
     * Vincular/Desvincular professor de turma
     */
    @Transactional
    public void vincularProfessor(Long turmaId, Long professorId) {
        var turma = buscarTurmaPorId(turmaId);
        var professor = buscarProfessorPorId(professorId);

        validadoresVinculoProfessor.forEach(validador -> validador.validar(turma, professor));

        turma.vincularProfessor(professor);
        turmaRepository.save(turma);
    }

    @Transactional
    public void desvincularProfessor(Long turmaId, Long professorId) {
        var turma = buscarTurmaPorId(turmaId);
        var professor = buscarProfessorPorId(professorId);

        validadoresDesvinculoProfessor.forEach(validador -> validador.validar(turma, professor));

        turma.desvincularProfessor();
        turmaRepository.save(turma);
    }

    /**
     * Vincular/Desvincular curso de turma
     */
    @Transactional
    public void vincularCurso(Long turmaId, Long cursoId) {
        var turma = buscarTurmaPorId(turmaId);
        var curso = buscarCursoPorId(cursoId);

        validadoresVinculoCurso.forEach(validador -> validador.validar(turma, curso));

        turma.vincularCurso(curso);
        turmaRepository.save(turma);
    }

    @Transactional
    public void desvincularCurso(Long turmaId, Long cursoId){
        var turma = buscarTurmaPorId(turmaId);
        var curso = buscarCursoPorId(cursoId);

        validadoresDesvinculoCurso.forEach(validador -> validador.validar(turma, curso));

        turma.desvincularCurso();
        turmaRepository.save(turma);
    }

    /**
     * Auxiliares
     */
    private Turma buscarTurmaPorId(Long id) {
        return turmaRepository.findById(id)
                .orElseThrow(() -> new ValidacaoException("Turma com ID " + id + " não encontrada"));
    }

    private Professor buscarProfessorPorId(Long id) {
        return professorRepository.findById(id)
                .orElseThrow(() -> new ValidacaoException("Professor com ID " + id + " não encontrado"));
    }

    private Curso buscarCursoPorId(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new ValidacaoException("Curso com ID " + id + " não encontrado"));
    }

    public Turma criarTurma(TurmaCreateDTO dto) {
        return new Turma(
                dto.codigo(),
                dto.dataInicio(),
                dto.dataFim(),
                dto.horarioInicio(),
                dto.horarioFim(),
                dto.vagasTotais(),
                dto.modalidade()
        );
    }
}
