package com.edutech.api.domain.professor.service;

import com.edutech.api.domain.endereco.mapper.EnderecoMapper;
import com.edutech.api.domain.enums.Modalidade;
import com.edutech.api.domain.exception.ValidacaoException;
import com.edutech.api.domain.professor.Professor;
import com.edutech.api.domain.professor.dto.ProfessorCreateDTO;
import com.edutech.api.domain.professor.dto.ProfessorDetalhesDTO;
import com.edutech.api.domain.professor.dto.ProfessorResumoDTO;
import com.edutech.api.domain.professor.dto.ProfessorUpdateDTO;
import com.edutech.api.domain.professor.mapper.ProfessorMapper;
import com.edutech.api.domain.professor.repository.ProfessorRepository;
import com.edutech.api.domain.professor.validacoes.ValidadorCadastroProfessor;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfessorService {

    private final ProfessorMapper professorMapper;
    private final EnderecoMapper enderecoMapper;
    private final ProfessorRepository professorRepository;
    private final List<ValidadorCadastroProfessor> validadores;

    @Transactional
    public ProfessorResumoDTO cadastrarProfessor(ProfessorCreateDTO dto){
        validadores.forEach(validador -> validador.validar(dto));

        var endereco = enderecoMapper.toEndereco(dto.endereco());

        var professor = new Professor(
                dto.nome(),
                dto.email(),
                dto.dataNascimento(),
                dto.telefone(),
                dto.cpf(),
                dto.modalidade(),
                endereco
        );

        professorRepository.save(professor);
        return professorMapper.toResumoDTO(professor);
    }

    @Transactional
    public ProfessorResumoDTO atualizarProfessor(Long id, ProfessorUpdateDTO dto) {
        var professor = buscarProfessorPorId(id);

        var endereco = enderecoMapper.toEndereco(dto.endereco());

        professor.atualizar(
                dto.nome(),
                dto.email(),
                dto.dataNascimento(),
                dto.telefone(),
                dto.status(),
                dto.modalidade(),
                endereco
        );

        professorRepository.save(professor);
        return professorMapper.toResumoDTO(professor);
    }

    public ProfessorResumoDTO buscarPorId(Long id) {
        var professor = buscarProfessorPorId(id);
        return professorMapper.toResumoDTO(professor);
    }

    public List<ProfessorResumoDTO> buscarProfessoresPorNome(String nome) {
        List<Professor> professores = professorRepository.findByNome(nome);

        if (professores.isEmpty()) {
            throw new ValidacaoException("Professor não encontrado");
        }

        return professores.stream()
                .map(professorMapper::toResumoDTO)
                .collect(Collectors.toList());
    }

    public List<ProfessorResumoDTO> buscarProfessoresPorModalidade(Modalidade modalidade) {
        List<Professor> professores = professorRepository.findByModalidade(modalidade);

        return professores.stream()
                .map(professorMapper::toResumoDTO)
                .toList();
    }

    public Page<ProfessorResumoDTO> buscarTodosProfessores(Pageable pageable) {
        Page<Professor> professores = professorRepository.findAll(pageable);
        return professores.map(professorMapper::toResumoDTO);
    }

    public ProfessorDetalhesDTO detalharProfessor(Long id){
        var professor = buscarProfessorPorId(id);
        return professorMapper.toDetalhesDTO(professor);
    }

    public void excluirProfessor(Long id){
        var professor = buscarProfessorPorId(id);

        professor.excluir();
        professorRepository.save(professor);
    }

    /**
     * Auxiliares
     */
    private Professor buscarProfessorPorId(Long id){
        return professorRepository.findById(id)
                .orElseThrow(() -> new ValidacaoException("Professor com ID " + id + " não encontrado"));
    }
}
