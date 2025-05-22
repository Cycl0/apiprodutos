package com.apiprodutos.controller;

import com.apiprodutos.model.Categoria;
import com.apiprodutos.model.Produto;
import com.apiprodutos.repository.ProdutoRepository;
import com.apiprodutos.service.CategoriaService;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.*;
import io.swagger.v3.oas.annotations.parameters.*;

import com.apiprodutos.dto.DescontoResponse;
import com.apiprodutos.exception.ErrorResponse;
import com.apiprodutos.exception.RegraNegocioException;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {
    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @Operation(summary = "Listar todas as categorias", description = "Retorna todas as categorias cadastradas.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de categorias retornada com sucesso",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Categoria.class))))
    })
    @GetMapping
    public List<Categoria> listarCategorias() {
        return categoriaService.listarTodas();
    }

    @Operation(summary = "Buscar categoria por ID", description = "Retorna uma categoria pelo seu ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoria encontrada",
            content = @Content(schema = @Schema(implementation = Categoria.class))),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> buscarCategoria(
        @Parameter(description = "ID da categoria", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.buscarPorId(id));
    }

    @Operation(
        summary = "Listar produtos de uma categoria",
        description = "Retorna todos os produtos vinculados a uma determinada categoria pelo ID da categoria."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Lista de produtos da categoria retornada com sucesso",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Produto.class)))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Requisição inválida",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Categoria não encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))
        )
    })
    @GetMapping("/{id}/produtos")
    public ResponseEntity<List<Produto>> listarProdutorPorCategoria(
        @Parameter(description = "ID da categoria", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(categoriaService.listarProdutosPorCategoria(id));
    }

    @Operation(summary = "Buscar categorias por nome", description = "Busca categorias cujo nome contenha o texto informado (case insensitive). Retorna lista vazia se nada for encontrado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de categorias encontrada",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Categoria.class))))
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<Categoria>> buscarCategoria(
        @Parameter(description = "Nome da categoria para busca", example = "Informática") @RequestParam String nome) {
        return ResponseEntity.ok(categoriaService.buscarPorNome(nome));
    }

    @Operation(summary = "Criar nova categoria", description = "Cadastra uma nova categoria. Não permite nomes duplicados e impede id duplicado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoria criada com sucesso",
            content = @Content(schema = @Schema(implementation = Categoria.class))),
        @ApiResponse(responseCode = "400", description = "Nome da categoria já existe ou id já existe",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<Categoria> criarCategoria(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados da categoria a ser criada", required = true,
            content = @Content(schema = @Schema(implementation = Categoria.class)))
        @RequestBody Categoria categoria) {
        Categoria salva = categoriaService.criarCategoria(categoria);
        return ResponseEntity.ok(salva);
    }

    @Operation(summary = "Atualizar categoria", description = "Atualiza o nome de uma categoria existente pelo ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso",
            content = @Content(schema = @Schema(implementation = Categoria.class))),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> atualizarCategoria(
        @Parameter(description = "ID da categoria", example = "1") @PathVariable Long id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados da categoria a ser atualizada", required = true,
            content = @Content(schema = @Schema(implementation = Categoria.class)))
        @Valid @RequestBody Categoria categoria) {
        Categoria salva = categoriaService.atualizarCategoria(id, categoria);
        return ResponseEntity.ok(salva);
    }

    @Operation(summary = "Deletar categoria", description = "Remove uma categoria pelo ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Categoria removida com sucesso"),
        @ApiResponse(responseCode = "404", description = "Categoria não encontrada",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarCategoria(
        @Parameter(description = "ID da categoria", example = "1") @PathVariable Long id) {
        categoriaService.deletarCategoria(id);
        return ResponseEntity.noContent().build();
    }
}



