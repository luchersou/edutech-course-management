package com.edutech.api.controller;

import com.edutech.api.domain.turma.dto.*;
import com.edutech.api.domain.turma.service.TurmaService;
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

@RestController
@RequestMapping("/turmas")
@RequiredArgsConstructor
public class TurmaController {

    private final TurmaService turmaService;

    @Operation(
            summary = "Cadastrar nova turma",
            description = "Cria uma nova turma com os dados fornecidos e retorna os dados resumidos"
    )
    @PostMapping
    public ResponseEntity<TurmaResumoDTO> cadastrar(@RequestBody @Valid TurmaCreateDTO dto,
                                                    UriComponentsBuilder uriBuilder) {

        var turmaResumo = turmaService.cadastrarTurma(dto);

        URI uri = uriBuilder.path("/turmas/{id}")
                .buildAndExpand(turmaResumo.id())
                .toUri();

        return ResponseEntity.created(uri).body(turmaResumo);
    }

    @Operation(
            summary = "Atualizar turma",
            description = "Atualiza os dados de uma turma existente com base no ID fornecido"
    )
    @PatchMapping("/{id}")
    public ResponseEntity<TurmaResumoDTO> atualizar(@PathVariable Long id,
                                                    @RequestBody @Valid TurmaUpdateDTO dto) {
        var turmaAtualizada = turmaService.atualizarTurma(id, dto);
        return ResponseEntity.ok(turmaAtualizada);
    }

    @Operation(
            summary = "Detalhar turma por ID",
            description = "Retorna todos os dados detalhados de uma turma especifica"
    )
    @GetMapping("/{id}")
    public ResponseEntity<TurmaDetalhesDTO> detalharPorId(@PathVariable Long id) {
        var detalhesDTO = turmaService.detalharPorId(id);
        return ResponseEntity.ok(detalhesDTO);
    }

    @Operation(
            summary = "Buscar turma por código",
            description = "Localiza uma turma especifica pelo seu código unico"
    )
    @GetMapping("/codigo")
    public ResponseEntity<TurmaResumoDTO> buscarPorCodigo(@RequestParam String codigo) {
        var resumoDTO = turmaService.buscarPorCodigo(codigo);
        return ResponseEntity.ok(resumoDTO);
    }

    @Operation(
            summary = "Listar todas as turmas",
            description = "Retorna uma lista paginada de todas as turmas, ordenadas por nome"
    )
    @GetMapping
    public Page<TurmaResumoDTO> buscarTodasTurmas(
            @PageableDefault(size = 10, sort = "dataInicio", direction = Sort.Direction.ASC) Pageable pageable){
        return turmaService.buscarTodasTurmas(pageable);
    }

    @GetMapping("/{id}/matriculas")
    public ResponseEntity<TurmaComMatriculasDTO> buscarTurmaComMatriculas(@PathVariable Long id) {
        var dto = turmaService.buscarTurmaComMatriculas(id);
        return ResponseEntity.ok(dto);
    }

    @Operation(
            summary = "Iniciar turma",
            description = "Altera o status de uma turma de ABERTA para EM_ANDAMENTO. Só é possível iniciar turmas que estão com status ABERTA e cuja data de início seja hoje ou anterior a data atual."
    )
    @PostMapping("/{turmaId}/iniciar")
    public ResponseEntity<Void> iniciarTurma(@PathVariable Long turmaId) {
        turmaService.iniciarTurma(turmaId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Concluir turma",
            description = "Altera o status de uma turma de EM_ANDAMENTO para CONCLUIDA. Só é possível concluir turmas que estão com status EM_ANDAMENTO."
    )
    @PostMapping("/{turmaId}/concluir")
    public ResponseEntity<Void> concluirTurma(@PathVariable Long turmaId) {
        turmaService.concluirTurma(turmaId);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Cancelar turma",
            description = "Realiza o cancelamento de uma turma especifica pelo ID"
    )
    @DeleteMapping("/{id}/cancelamento")
    public ResponseEntity<Void> cancelarTurma(@PathVariable Long id) {
        turmaService.cancelarTurma(id);
        return ResponseEntity.noContent().build();
    }


    /**
     * Vincular/Desvincular professor de turma
     */
    @Operation(
            summary = "Vincular professor a turma",
            description = "Associa um professor existente a uma turma especifica"
    )
    @PutMapping("/{id}/professor/{professorId}")
    public ResponseEntity<Void> vincularProfessor(@PathVariable Long id,
                                                  @PathVariable Long professorId) {
        turmaService.vincularProfessor(id, professorId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Desvincular professor da turma",
            description = "Remove a associação entre um professor e uma turma especifica"
    )
    @DeleteMapping("/{id}/professor/{professorId}")
    public ResponseEntity<Void> desvincularProfessor(@PathVariable Long id,
                                                     @PathVariable Long professorId) {
        turmaService.desvincularProfessor(id, professorId);
        return ResponseEntity.noContent().build();
    }


    /**
     * Vincular/Desvincular curso de turma
     */
    @Operation(
            summary = "Vincular curso a turma",
            description = "Associa um curso existente a uma turma especifica"
    )
    @PutMapping("/{id}/curso/{cursoId}")
    public ResponseEntity<Void> vincularCurso(@PathVariable Long id,
                                              @PathVariable Long cursoId) {
        turmaService.vincularCurso(id, cursoId);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Desvincular curso da turma",
            description = "Remove a associação entre um curso e uma turma especifica"
    )
    @DeleteMapping("/{id}/curso/{cursoId}")
    public ResponseEntity<Void> desvincularCurso(@PathVariable Long id,
                                                 @PathVariable Long cursoId) {
        turmaService.desvincularCurso(id, cursoId);
        return ResponseEntity.noContent().build();
    }
}
