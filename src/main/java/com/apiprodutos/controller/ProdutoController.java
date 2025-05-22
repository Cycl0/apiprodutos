package com.apiprodutos.controller;

import com.apiprodutos.model.Produto;
import com.apiprodutos.model.Categoria;
import com.apiprodutos.repository.ProdutoRepository;
import com.apiprodutos.repository.CategoriaRepository;

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

@RestController
@RequestMapping("/produtos")
public class ProdutoController {
    private final ProdutoRepository produtoRepository;
    private final CategoriaRepository categoriaRepository;

    public ProdutoController(ProdutoRepository produtoRepository, CategoriaRepository categoriaRepository) {
        this.produtoRepository = produtoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    @Operation(summary = "Listar todos os produtos", description = "Retorna todos os produtos cadastrados.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Produto.class))))
    })
    @GetMapping
    public List<Produto> listarProdutos() {
        return produtoRepository.findAll();
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
        Produto produto = produtoRepository.findById(id)
            .orElse(null);
        if (produto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(produto);
    }

    @Operation(summary = "Buscar produtos por nome", description = "Busca produtos cujo nome contenha o texto informado (case insensitive). Retorna lista vazia se nada for encontrado.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de produtos encontrada",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Produto.class))))
    })
    @GetMapping("/buscar")
    public ResponseEntity<List<Produto>> buscarProduto(
        @Parameter(description = "Nome do produto para busca", example = "Notebook") @RequestParam String nome) {
        return ResponseEntity.ok(produtoRepository.findByNomeContainingIgnoreCase(nome));
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
        Produto produto = produtoRepository.findById(id)
            .orElse(null);
        if (produto == null) {
            throw new RuntimeException("Produto não encontrado.");
        }
        if (percentual < 0 || percentual > 50) {
            throw new RuntimeException("O percentual de desconto deve ser entre 0% e 50%.");
        }

        double precoOriginal = produto.getPreco();
        double precoFinal = precoOriginal * (1 - percentual / 100.0);
        String descontoAplicado = percentual + "%";

        return ResponseEntity.ok(
            new DescontoResponse(produto.getNome(), precoOriginal, descontoAplicado, precoFinal)
        );
    }

    @Operation(summary = "Criar novo produto", description = "Cadastra um novo produto. Não permite nomes duplicados e exige categoria existente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Produto criado com sucesso",
            content = @Content(schema = @Schema(implementation = Produto.class))),
        @ApiResponse(responseCode = "400", description = "Nome do produto já existe, categoria não encontrada ou regra de negócio violada",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<Produto> criarProduto(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Dados do produto a ser criado", required = true,
            content = @Content(schema = @Schema(implementation = Produto.class)))
        @Valid @RequestBody Produto produto,
        @Parameter(description = "ID da categoria do produto", example = "1") @RequestParam Long categoriaId) {

        Categoria categoria = categoriaRepository.findById(categoriaId)
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada."));
        produto.setCategoria(categoria);

        boolean produtoExistente = produtoRepository.findAll().stream()
            .anyMatch(p -> p.getNome().equalsIgnoreCase(produto.getNome()));
        if (produtoExistente) {
            throw new RuntimeException("Já existe um produto com esse nome.");
        }

        if (produto.getNome() != null && produto.getPreco() != null) {
            if (produto.getNome().toLowerCase().contains("promoção") && produto.getPreco() >= 500) {
                throw new RuntimeException("O preço de produtos em promoção deve ser menor que R$ 500,00.");
            }
        }

        Produto salvo = produtoRepository.save(produto);
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

        Produto existente = produtoRepository.findById(id)
            .orElse(null);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }
        Categoria categoria = categoriaRepository.findById(categoriaId)
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada."));
        produto.setCategoria(categoria);

        boolean nomeDuplicado = produtoRepository.findAll().stream()
            .anyMatch(p -> !p.getId().equals(id) && p.getNome().equalsIgnoreCase(produto.getNome()));
        if (nomeDuplicado) {
            throw new RuntimeException("Já existe um produto com esse nome.");
        }
        if (produto.getNome() != null && produto.getPreco() != null) {
            if (produto.getNome().toLowerCase().contains("promoção") && produto.getPreco() >= 500) {
                throw new RuntimeException("O preço de produtos em promoção deve ser menor que R$ 500,00.");
            }
        }
        produto.setId(id);
        Produto salvo = produtoRepository.save(produto);
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
        if (!produtoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        produtoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}