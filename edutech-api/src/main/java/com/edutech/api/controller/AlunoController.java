package com.edutech.api.controller;

import com.edutech.api.domain.aluno.dto.AlunoCreateDTO;
import com.edutech.api.domain.aluno.dto.AlunoDetalhesDTO;
import com.edutech.api.domain.aluno.dto.AlunoResumoDTO;
import com.edutech.api.domain.aluno.dto.AlunoUpdateDTO;
import com.edutech.api.domain.aluno.enums.StatusAluno;
import com.edutech.api.domain.aluno.mapper.AlunoMapper;
import com.edutech.api.domain.aluno.service.AlunoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alunos")
public class AlunoController {

    private final AlunoService alunoService;

    @Operation(
            summary = "Cadastrar um novo aluno",
            description = "Registra um aluno no sistema com dados como nome, email, telefone, CPF, data de nascimento e endereço"
    )
    @PostMapping
    public ResponseEntity<AlunoResumoDTO> cadastrarAluno(@RequestBody @Valid AlunoCreateDTO dto,
                                                         UriComponentsBuilder uriBuilder){
        var aluno = alunoService.cadastrarAluno(dto);

        URI uri = uriBuilder.path("/alunos/{id}")
                .buildAndExpand(aluno.id())
                .toUri();

        return ResponseEntity.created(uri).body(aluno);
    }

    @Operation(
            summary = "Atualizar um aluno existente",
            description = "Atualiza os dados de um aluno existente com base no ID informado. Campos como nome, email, endereço, status e data de nascimento podem ser alterados."
    )
    @PatchMapping("/{id}")
    public ResponseEntity<AlunoResumoDTO> atualizar(@PathVariable Long id,
                                                    @RequestBody @Valid AlunoUpdateDTO dto) {
        AlunoResumoDTO atualizado = alunoService.atualizarAluno(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @Operation(
            summary = "Buscar aluno por ID",
            description = "Retorna os dados resumidos de um aluno com base no ID informado."
    )
    @GetMapping("/{id}")
    public ResponseEntity<AlunoResumoDTO> buscarAlunoPorId(@PathVariable Long id) {
        var aluno = alunoService.buscarAlunoPorId(id);
        return ResponseEntity.ok(aluno);
    }

    @Operation(
            summary = "Buscar alunos pelo nome",
            description = "Retorna uma lista de alunos cujo nome contenha o valor informado, ignorando letras maiusculas/minusculas."
    )
    @GetMapping("/nome")
    public ResponseEntity<List<AlunoResumoDTO>> buscarAlunosPorNome(@RequestParam String nome) {
        List<AlunoResumoDTO> alunos = alunoService.buscarAlunoPorNome(nome);
        return ResponseEntity.ok(alunos);
    }

    @Operation(
            summary = "Buscar alunos por status",
            description = "Retorna uma lista paginada de alunos filtrados por status (ATIVO, INATIVO, FORMADO, CANCELADO), ordenados pelo nome."
    )
    @GetMapping("/status")
    public ResponseEntity<Page<AlunoResumoDTO>> buscarAlunosPorStatus(@RequestParam StatusAluno status,
                                                                      @PageableDefault(size = 10, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<AlunoResumoDTO> alunos = alunoService.buscarAlunosPorStatus(status, pageable);
        return ResponseEntity.ok(alunos);
    }

    @Operation(
            summary = "Listar todos os alunos",
            description = "Retorna uma lista paginada de todos os alunos cadastrados no sistema, ordenados pelo nome."
    )
    @GetMapping
    public ResponseEntity<Page<AlunoResumoDTO>> buscarTodosAlunos(
            @PageableDefault(size = 11, sort = "nome") Pageable pageable) {
        Page<AlunoResumoDTO> alunos = alunoService.buscarTodosAlunos(pageable);
        return ResponseEntity.ok(alunos);
    }

    @Operation(
            summary = "Detalhar aluno por ID",
            description = "Retorna informações detalhadas de um aluno, incluindo dados adicionais que não estão presentes no resumo."
    )
    @GetMapping("/{id}/detalhes")
    public ResponseEntity<AlunoDetalhesDTO> detalharAluno(@PathVariable Long id) {
        AlunoDetalhesDTO aluno = alunoService.detalharAluno(id);
        return ResponseEntity.ok(aluno);
    }

    @Operation(
            summary = "Excluir aluno",
            description = "Remove um aluno do sistema com base no ID informado."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirAluno(@PathVariable Long id) {
        alunoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}