package com.edutech.api.controller;

import com.edutech.api.domain.enums.Modalidade;
import com.edutech.api.domain.professor.dto.ProfessorCreateDTO;
import com.edutech.api.domain.professor.dto.ProfessorDetalhesDTO;
import com.edutech.api.domain.professor.dto.ProfessorResumoDTO;
import com.edutech.api.domain.professor.dto.ProfessorUpdateDTO;
import com.edutech.api.domain.professor.service.ProfessorService;
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
@RequestMapping("/professores")
@RequiredArgsConstructor
public class ProfessorController {

    private final ProfessorService professorService;

    @Operation(
            summary = "Cadastrar novo professor",
            description = "Registra um novo professor no sistema e retorna seus dados resumidos"
    )
    @PostMapping
    public ResponseEntity<ProfessorResumoDTO> cadastrarProfessor(@RequestBody @Valid ProfessorCreateDTO dto,
                                                                   UriComponentsBuilder uriBuilder) {

        var professor = professorService.cadastrarProfessor(dto);

        URI uri = uriBuilder.path("/professores/{id}")
                .buildAndExpand(professor.id())
                .toUri();

        return ResponseEntity.created(uri).body(professor);
    }

    @Operation(
            summary = "Atualizar dados do professor",
            description = "Atualiza as informações de um professor existente pelo ID"
    )
    @PatchMapping("/{id}")
    public ResponseEntity<ProfessorResumoDTO> atualizar(@PathVariable Long id,
                                                          @RequestBody @Valid ProfessorUpdateDTO dto) {
        ProfessorResumoDTO atualizado = professorService.atualizarProfessor(id, dto);
        return ResponseEntity.ok(atualizado);
    }

    @Operation(
            summary = "Buscar professor por ID",
            description = "Obtém os dados resumidos de um professor especifico pelo seu ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProfessorResumoDTO> buscarProfessorPorId(@PathVariable Long id) {
        var professor = professorService.buscarPorId(id);
        return ResponseEntity.ok(professor);
    }

    @Operation(
            summary = "Buscar professores por nome",
            description = "Lista professores cujo nome contém o termo pesquisado (busca por nome)"
    )
    @GetMapping("/buscar")
    public ResponseEntity<List<ProfessorResumoDTO>> buscarProfessoresPorNome(@RequestParam String nome) {
        List<ProfessorResumoDTO> professores = professorService.buscarProfessoresPorNome(nome);
        return ResponseEntity.ok(professores);
    }

    @Operation(
            summary = "Buscar professores por modalidade",
            description = "Lista professores filtrados por modalidade de ensino (PRESENCIAL, REMOTO, HIBRIDO)"
    )
    @GetMapping("/modalidade")
    public ResponseEntity<List<ProfessorResumoDTO>> buscarPorModalidade(@RequestParam Modalidade modalidade) {
        List<ProfessorResumoDTO> professores = professorService.buscarProfessoresPorModalidade(modalidade);
        return ResponseEntity.ok(professores);
    }

    @Operation(
            summary = "Listar todos os professores",
            description = "Retorna uma lista paginada de todos professores, ordenados por nome"
    )
    @GetMapping
    public Page<ProfessorResumoDTO> buscarTodosProfessores(
            @PageableDefault(size = 11, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        return professorService.buscarTodosProfessores(pageable);
    }

    @Operation(
            summary = "Detalhar professor",
            description = "Obtém todos os dados detalhados de um professor especifico, incluindo informações completas"
    )
    @GetMapping("/{id}/detalhes")
    public ResponseEntity<ProfessorDetalhesDTO> detalharProfessor(@PathVariable Long id) {
        var professor = professorService.detalharProfessor(id);
        return ResponseEntity.ok(professor);
    }

    @Operation(
            summary = "Excluir professor",
            description = "Inativa um professor do sistema pelo seu ID"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        professorService.excluirProfessor(id);
        return ResponseEntity.noContent().build();
    }
}
