package com.edutech.api.controller;

import com.edutech.api.domain.matricula.dto.*;
import com.edutech.api.domain.matricula.enums.MotivoCancelamento;
import com.edutech.api.domain.matricula.service.MatriculaService;
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
@RequestMapping("/matriculas")
@RequiredArgsConstructor
public class MatriculaController {

    private final MatriculaService matriculaService;

    @Operation(
            summary = "Cadastrar nova matricula",
            description = "Cria uma nova matricula de aluno em uma turma e retorna os dados resumidos da matricula"
    )
    @PostMapping
    public ResponseEntity<MatriculaResumoDTO> cadastrar(@RequestBody @Valid MatriculaCreateDTO dto,
                                                        UriComponentsBuilder uriBuilder) {

        var matriculaResumo = matriculaService.cadastrarMatricula(dto);

        URI uri = uriBuilder.path("/matriculas/{id}")
                .buildAndExpand(matriculaResumo.id())
                .toUri();

        return ResponseEntity.created(uri).body(matriculaResumo);
    }

    @Operation(
            summary = "Detalhar matricula por ID",
            description = "Retorna todos os dados detalhados de uma matricula especifica"
    )
    @GetMapping("/{id}")
    public ResponseEntity<MatriculaDetalhesDTO> detalharPorId(@PathVariable Long id) {
        var matriculaDetalhesDTO = matriculaService.detalharPorId(id);
        return ResponseEntity.ok().body(matriculaDetalhesDTO);
    }

    @Operation(
            summary = "Buscar matriculas por nome do aluno",
            description = "Retorna lista de matriculas associadas a um aluno (busca pelo nome)"
    )
    @GetMapping("/buscar-por-nome")
    public ResponseEntity<List<MatriculaResumoDTO>> buscarPorNome(@RequestParam String nome) {
        var matriculaResumoDTO = matriculaService.buscarPorNomeDoAluno(nome);
        return ResponseEntity.ok().body(matriculaResumoDTO);
    }

    @Operation(
            summary = "Listar todas as matriculas",
            description = "Retorna uma lista paginada de todas as matriculas, ordenadas por data de matricula"
    )
    @GetMapping
    public Page<MatriculaResumoDTO> listarTodos(
            @PageableDefault(size = 10, sort = "dataMatricula", direction = Sort.Direction.ASC) Pageable pageable){
        return matriculaService.buscarTodasMatriculas(pageable);
    }

    @Operation(
            summary = "Concluir matricula",
            description = "Conclui matricula ativa do aluno se a nota dele for maior de 7 e retorna um resumo de seus dados"
    )
    @PutMapping("/{id}/concluir")
    public ResponseEntity<MatriculaResumoDTO> concluir(@PathVariable Long id,
                                                       @RequestBody @Valid ConcluirMatriculaDTO dto) {
        var resultado = matriculaService.concluirMatricula(id, dto.notaFinal());
        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Trancar a matricula",
            description = "Tranca a matricula do aluno através de seu ID e retorna um resumo dos dados dele"
    )
    @PutMapping("/{id}/trancar")
    public ResponseEntity<MatriculaResumoDTO> trancar(@PathVariable Long id) {
        var resultado = matriculaService.trancarMatricula(id);
        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Reativar matricula",
            description = "Reativa a matricula do aluno deixando ela com status ATIVO"
    )
    @PutMapping("/{id}/reativar")
    public ResponseEntity<MatriculaResumoDTO> reativar(@PathVariable Long id) {
        var resultado = matriculaService.reativarMatricula(id);
        return ResponseEntity.ok(resultado);
    }

    @Operation(
            summary = "Cancelar matricula",
            description = "Realiza o cancelamento de uma matricula especifica, exigindo um motivo para o cancelamento"
    )
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<MatriculaResumoDTO> cancelar(@PathVariable Long id,
                                                       @RequestBody @Valid CancelarMatriculaDTO dto) {

        var resultado = matriculaService.cancelarMatricula(id, dto.motivo());
        return ResponseEntity.ok(resultado);
    }
}
