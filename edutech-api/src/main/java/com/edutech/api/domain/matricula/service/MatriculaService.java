package com.edutech.api.domain.matricula.service;

import com.edutech.api.domain.aluno.Aluno;
import com.edutech.api.domain.aluno.repository.AlunoRepository;
import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.matricula.Matricula;
import com.edutech.api.domain.matricula.dto.MatriculaCreateDTO;
import com.edutech.api.domain.matricula.dto.MatriculaDetalhesDTO;
import com.edutech.api.domain.matricula.dto.MatriculaResumoDTO;
import com.edutech.api.domain.matricula.enums.MotivoCancelamento;
import com.edutech.api.domain.matricula.mapper.MatriculaMapper;
import com.edutech.api.domain.matricula.repository.MatriculaRepository;
import com.edutech.api.domain.matricula.validadores.ValidadorCadastroMatricula;
import com.edutech.api.domain.turma.Turma;
import com.edutech.api.domain.turma.repository.TurmaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final MatriculaMapper matriculaMapper;
    private final AlunoRepository alunoRepository;
    private final TurmaRepository turmaRepository;
    private final List<ValidadorCadastroMatricula> validadoresCadastroMatricula;

    @Transactional
    public MatriculaResumoDTO cadastrarMatricula(MatriculaCreateDTO dto){
        var turma = buscarTurmaPorId(dto.turmaId());
        var aluno = buscarAlunoPorId(dto.alunoId());

        if(turma.getCurso() == null){
            throw new ValidacaoException("A turma com ID '" + dto.turmaId() + "' não tem um curso associado. Não é possível realizar a matrícula.");
        }

        validadoresCadastroMatricula.forEach(v -> v.validar(dto));

        var matricula = new Matricula(aluno, turma, dto.dataMatricula());

        matriculaRepository.save(matricula);
        return matriculaMapper.toResumoDTO(matricula);
    }

    public MatriculaDetalhesDTO detalharPorId(Long id){
        var matricula = buscarMatriculaPorId(id);

        return matriculaMapper.toDetalhesDTO(matricula);
    }

    public List<MatriculaResumoDTO> buscarPorNomeDoAluno(String nome){
        if (nome == null || nome.trim().isEmpty()) {
            throw new ValidacaoException("Nome do aluno é obrigatório.");
        }

        var matriculas = matriculaRepository.findByAlunoNome(nome);

        if (matriculas.isEmpty()) {
            throw new ValidacaoException("Aluno não possui matricula cadastrada: " + nome);
        }

        return matriculas.stream()
                .map(matriculaMapper::toResumoDTO)
                .collect(Collectors.toList());
    }

    public Page<MatriculaResumoDTO> buscarTodasMatriculas(Pageable pageable){
        Page<Matricula> matriculas = matriculaRepository.findAll(pageable);
        return matriculas.map(matriculaMapper::toResumoDTO);
    }

    @Transactional
    public MatriculaResumoDTO concluirMatricula(Long matriculaId, BigDecimal nota) {
        var matricula = buscarMatriculaPorId(matriculaId);
        matricula.concluir(nota);
        matriculaRepository.save(matricula);
        return matriculaMapper.toResumoDTO(matricula);
    }

    @Transactional
    public MatriculaResumoDTO trancarMatricula(Long matriculaId) {
        var matricula = buscarMatriculaPorId(matriculaId);
        matricula.trancar();
        matriculaRepository.save(matricula);
        return matriculaMapper.toResumoDTO(matricula);
    }

    @Transactional
    public MatriculaResumoDTO reativarMatricula(Long matriculaId) {
        var matricula = buscarMatriculaPorId(matriculaId);
        matricula.reativar();
        matriculaRepository.save(matricula);
        return matriculaMapper.toResumoDTO(matricula);
    }

    @Transactional
    public MatriculaResumoDTO cancelarMatricula(Long id, MotivoCancelamento motivo) {
        var matricula = buscarMatriculaPorId(id);
        matricula.cancelar(motivo);
        matriculaRepository.save(matricula);

        return matriculaMapper.toResumoDTO(matricula);
    }

    /**
     * Auxiliares
     */
    private Turma buscarTurmaPorId(Long id) {
        return turmaRepository.findById(id)
                .orElseThrow(() -> new ValidacaoException("Turma com ID " + id + " não encontrada"));
    }

    private Aluno buscarAlunoPorId(Long id) {
        return alunoRepository.findById(id)
                .orElseThrow(() -> new ValidacaoException("Aluno com ID " + id + " não encontrado"));
    }

    private Matricula buscarMatriculaPorId(Long id){
        return matriculaRepository.findById(id)
                .orElseThrow(() -> new ValidacaoException("Matricula com ID " + id + " não encontrado"));
    }

}
