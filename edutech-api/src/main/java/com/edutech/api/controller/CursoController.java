package com.edutech.api.controller;

import com.edutech.api.domain.curso.dto.CursoCreateDTO;
import com.edutech.api.domain.curso.dto.CursoDetalhesDTO;
import com.edutech.api.domain.curso.dto.CursoResumoDTO;
import com.edutech.api.domain.curso.dto.CursoUpdateDTO;
import com.edutech.api.domain.curso.enums.NivelCurso;
import com.edutech.api.domain.curso.service.CursoService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;

    @Operation(
            summary = "Cadastrar novo curso",
            description = "Cria um novo curso com os dados fornecidos e retorna os dados resumidos"
    )
    @PostMapping
    public ResponseEntity<CursoResumoDTO> cadastrar(@RequestBody @Valid CursoCreateDTO dto,
                                                         UriComponentsBuilder uriBuilder) {

        var cursoResumo = cursoService.cadastrarCurso(dto);

        URI uri = uriBuilder.path("/cursos/{id}")
                .buildAndExpand(cursoResumo.id())
                .toUri();

        return ResponseEntity.created(uri).body(cursoResumo);
    }

    @Operation(
            summary = "Atualizar curso existente",
            description = "Atualiza os dados de um curso existente com base no ID fornecido"
    )
    @PatchMapping("/{id}")
    public ResponseEntity<CursoResumoDTO> atualizar(@PathVariable Long id,
                                                    @RequestBody @Valid CursoUpdateDTO dto) {

        var cursoAtualizado = cursoService.atualizarCurso(id, dto);
        return ResponseEntity.ok().body(cursoAtualizado);
    }

    @Operation(
            summary = "Listar todos os cursos",
            description = "Retorna uma lista paginada de todos os cursos cadastrados no sistema, ordenados pelo nome."
    )
    @GetMapping
    public ResponseEntity<Page<CursoResumoDTO>> buscarTodosCursos(
            @PageableDefault(size = 10, sort = "nome") Pageable pageable) {

        var cursos = cursoService.buscarTodosCursos(pageable);
        return ResponseEntity.ok(cursos);
    }

    @Operation(
            summary = "Buscar curso por ID",
            description = "Retorna os dados resumidos de um curso especifico com base no ID"
    )
    @GetMapping("/{id}")
    public ResponseEntity<CursoResumoDTO> buscarPorId(@PathVariable Long id) {
        var cursoResumo = cursoService.buscarPorId(id);
        return ResponseEntity.ok().body(cursoResumo);
    }

    @Operation(
            summary = "Detalhar curso por ID",
            description = "Retorna os dados completos de um curso especifico com base no ID"
    )
    @GetMapping("/{id}/detalhes")
    public ResponseEntity<CursoDetalhesDTO> detalharPorId(@PathVariable Long id) {
        var cursoDetalhesDTO = cursoService.detalharPorId(id);
        return ResponseEntity.ok().body(cursoDetalhesDTO);
    }

    @Operation(
            summary = "Buscar cursos por faixa de carga horaria",
            description = "Retorna lista de cursos filtrados por intervalo de carga horaria (minima e maxima)"
    )
    @GetMapping("/buscar-por-carga-horaria")
    public ResponseEntity<List<CursoResumoDTO>> buscarPorCargaHorariaIntervalo(@RequestParam(required = false) Integer cargaHorariaMin,
                                                                               @RequestParam(required = false) Integer cargaHorariaMax) {
        var lista = cursoService.buscarPorCargaHorariaIntervalo(cargaHorariaMin, cargaHorariaMax);
        return ResponseEntity.ok(lista);
    }

    @Operation(
            summary = "Buscar cursos por nível",
            description = "Retorna lista de cursos filtrados por nível (INTRODUÇÃO, BASICO, INTERMEDIÁRIO, AVANÇADO, ESPECIALIZAÇÃO)"
    )
    @GetMapping("/buscar-por-nivel")
    public ResponseEntity<List<CursoResumoDTO>> buscarPorNivel(@RequestParam NivelCurso nivel) {
        List<CursoResumoDTO> cursos = cursoService.buscarPorNivel(nivel);
        return ResponseEntity.ok(cursos);
    }

    @Operation(
            summary = "Buscar curso por nome exato",
            description = "Retorna os dados de um curso especifico com base no nome exato"
    )
    @GetMapping("/buscar-por-nome")
    public ResponseEntity<CursoResumoDTO> buscarPorNome(@RequestParam String nome) {
        var cursoResumoDTO = cursoService.buscarPorNome(nome);
        return ResponseEntity.ok().body(cursoResumoDTO);
    }

    @Operation(
            summary = "Ativar curso",
            description = "Ativa um curso especifico alterando seu status para ATIVO"
    )
    @PutMapping("/{id}/ativar")
    public ResponseEntity<Void> ativarCurso(@PathVariable Long id) {
        cursoService.ativarCurso(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Inativar curso",
            description = "Inativa um curso específico alterando seu status para INATIVO"
    )
    @PutMapping("/{id}/inativar")
    public ResponseEntity<Void> inativarCurso(@PathVariable Long id) {
        cursoService.inativarCurso(id);
        return ResponseEntity.ok().build();
    }

    /**
     * Vincular/Desvincular e listar professor de curso
     */
    @Operation(
            summary = "Vincular professor ao curso",
            description = "Associa um professor existente a um curso especifico"
    )
    @PutMapping("/{cursoId}/professor/{professorId}")
    public ResponseEntity<Void> vincularProfessor(@PathVariable Long cursoId,
                                                  @PathVariable Long professorId) {
        cursoService.vincularProfessor(cursoId, professorId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Desvincular professor do curso",
            description = "Remove a associação entre um professor e um curso especifico"
    )
    @DeleteMapping("/{cursoId}/professor/{professorId}")
    public ResponseEntity<Void> desvincularProfessor(@PathVariable Long cursoId,
                                                     @PathVariable Long professorId) {
        cursoService.desvincularProfessor(cursoId, professorId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Listar cursos do professor",
            description = "Retorna todos os cursos associados a um professor específico"
    )
    @GetMapping("/professor/{professorId}")
    public ResponseEntity<List<CursoResumoDTO>> listarCursosDoProfessor(@PathVariable Long professorId) {
        List<CursoResumoDTO> cursos = cursoService.listarCursosDoProfessor(professorId);
        return ResponseEntity.ok(cursos);
    }
}
