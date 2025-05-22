package com.apiprodutos.controller;

import com.apiprodutos.model.Produto;
import com.apiprodutos.model.Categoria;
import com.apiprodutos.repository.ProdutoRepository;
import com.apiprodutos.repository.CategoriaRepository;
import com.apiprodutos.service.ProdutoService;

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
@RequestMapping("/produtos")
public class ProdutoController {
    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @Operation(summary = "Listar todos os produtos", description = "Retorna todos os produtos cadastrados.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Produto.class))))
    })
    @GetMapping
    public List<Produto> listarProdutos() {
        return produtoService.listarTodos();
    }

    @Operation(summary = "Buscar produto por ID", description = "Retorna um produto pelo seu ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produto encontrado",
            content = @Content(schema = @Schema(implementation = Produto.class))),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarProduto(
        @Parameter(description = "ID do produto", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @Operation(summary = "Buscar produtos por nome", description = "Busca produtos cujo nome contenha o texto informado (case insensitive). Retorna lista vazia se nada for encontrado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de produtos encontrada",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Produto.class))))
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<Produto>> buscarProduto(
        @Parameter(description = "Nome do produto para busca", example = "Notebook") @RequestParam String nome) {
        return ResponseEntity.ok(produtoService.buscarPorNome(nome));
    }

    @Operation(summary = "Calcular preço com desconto", description = "Retorna o valor original e o valor com desconto de um produto, dado um percentual de desconto. Não permite descontos maiores que 50%.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cálculo realizado com sucesso",
            content = @Content(schema = @Schema(implementation = DescontoResponse.class))),
        @ApiResponse(responseCode = "400", description = "Requisição inválida ou desconto inválido",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}/desconto")
    public ResponseEntity<DescontoResponse> aplicarDesconto(
        @Parameter(description = "ID do produto", example = "1") @PathVariable Long id,
        @Parameter(description = "Percentual de desconto (0 a 50)", example = "10") @RequestParam Double percentual) {
        return ResponseEntity.ok(produtoService.aplicarDesconto(id, percentual));
    }

    @Operation(summary = "Criar novo produto", description = "Cadastra um novo produto. Não permite nomes duplicados, exige categoria existente e impede id duplicado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Produto criado com sucesso",
            content = @Content(schema = @Schema(implementation = Produto.class))),
        @ApiResponse(responseCode = "400", description = "Nome do produto já existe, categoria não encontrada, id já existe ou regra de negócio violada",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<Produto> criarProduto(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do produto a ser criado", required = true,
            content = @Content(schema = @Schema(implementation = Produto.class)))
        @Valid @RequestBody Produto produto,
        @Parameter(description = "ID da categoria do produto", example = "1") @RequestParam Long categoriaId) {
        Produto salvo = produtoService.criarProduto(produto, categoriaId);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    @Operation(summary = "Atualizar produto", description = "Atualiza um produto existente pelo ID. Não permite nomes duplicados e exige categoria existente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso",
            content = @Content(schema = @Schema(implementation = Produto.class))),
        @ApiResponse(responseCode = "400", description = "Nome do produto já existe, categoria não encontrada ou regra de negócio violada",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizarProduto(
        @Parameter(description = "ID do produto", example = "1") @PathVariable Long id,
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do produto a ser atualizado", required = true,
            content = @Content(schema = @Schema(implementation = Produto.class)))
        @Valid @RequestBody Produto produto,
        @Parameter(description = "ID da categoria do produto", example = "1") @RequestParam Long categoriaId) {
        Produto salvo = produtoService.atualizarProduto(id, produto, categoriaId);
        return ResponseEntity.ok(salvo);
    }

    @Operation(summary = "Deletar produto", description = "Remove um produto pelo ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Produto removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarProduto(
        @Parameter(description = "ID do produto", example = "1") @PathVariable Long id) {
        produtoService.deletarProduto(id);
        return ResponseEntity.noContent().build();
    }
}