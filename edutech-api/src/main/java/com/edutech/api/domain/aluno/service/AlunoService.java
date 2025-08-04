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
import com.edutech.api.domain.endereco.mapper.EnderecoMapper;
import com.edutech.api.domain.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlunoService {

    private final AlunoMapper alunoMapper;
    private final EnderecoMapper enderecoMapper;
    private final AlunoRepository alunoRepository;
    private final List<ValidadorCadastroAluno> validadores;

    @Transactional
    public AlunoResumoDTO cadastrarAluno(AlunoCreateDTO dto){
        validadores.forEach(validador -> validador.validar(dto));

        var endereco = enderecoMapper.toEndereco(dto.endereco());

        var aluno = new Aluno(
                dto.nome(),
                dto.email(),
                dto.telefone(),
                dto.cpf(),
                dto.dataDeNascimento(),
                endereco
        );

        alunoRepository.save(aluno);
        return alunoMapper.toResumoDTO(aluno);
    }

    @Transactional
    public AlunoResumoDTO atualizarAluno(Long id, AlunoUpdateDTO dto){
        var aluno = buscarAluno(id);

        var enderecoAtualizado = enderecoMapper.toEndereco(dto.endereco());

        aluno.atualizar(
                dto.nome(),
                dto.email(),
                dto.telefone(),
                dto.dataDeNascimento(),
                dto.status(),
                enderecoAtualizado
        );

        alunoRepository.save(aluno);
        return alunoMapper.toResumoDTO(aluno);
    }

    public AlunoResumoDTO buscarAlunoPorId(Long id){
        var aluno = buscarAluno(id);
        return alunoMapper.toResumoDTO(aluno);
    }

    public List<AlunoResumoDTO> buscarAlunoPorNome(String nome){
        List<Aluno> alunos = alunoRepository.findByNome(nome);

        if (alunos.isEmpty()) {
            throw new ValidacaoException("Aluno não encontrado");
        }

        return alunos.stream()
                .map(alunoMapper::toResumoDTO)
                .collect(Collectors.toList());
    }

    public Page<AlunoResumoDTO> buscarAlunosPorStatus(StatusAluno statusAluno, Pageable pageable) {
        Page<Aluno> alunos = alunoRepository.findByStatus(statusAluno, pageable);
        return alunos.map(alunoMapper::toResumoDTO);
    }

    public Page<AlunoResumoDTO> buscarTodosAlunos(Pageable pageable){
        Page<Aluno> alunos = alunoRepository.findAll(pageable);
        return alunos.map(alunoMapper::toResumoDTO);
    }

    public AlunoDetalhesDTO detalharAluno(Long id){
        var aluno = buscarAluno(id);
        return alunoMapper.toDetalhesDTO(aluno);
    }

    @Transactional
    public void excluir(Long id) {
        var aluno = buscarAluno(id);
        aluno.excluir();
        alunoRepository.save(aluno);
    }

    private Aluno buscarAluno(Long id){
        return alunoRepository.findById(id)
                .orElseThrow(() -> new ValidacaoException("Aluno não encontrado com o ID: " + id));
    }
}
