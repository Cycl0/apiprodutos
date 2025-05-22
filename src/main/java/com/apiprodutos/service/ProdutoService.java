package com.apiprodutos.service;

import com.apiprodutos.model.Produto;
import com.apiprodutos.model.Categoria;
import com.apiprodutos.repository.ProdutoRepository;
import com.apiprodutos.repository.CategoriaRepository;
import com.apiprodutos.dto.DescontoResponse;
import com.apiprodutos.exception.RegraNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Service
public class ProdutoService {
    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public Produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
            .orElseThrow(() -> new RegraNegocioException("Produto não encontrado."));
    }

    public List<Produto> buscarPorNome(String nome) {
        return produtoRepository.findByNomeContainingIgnoreCase(nome);
    }

    public Produto criarProduto(Produto produto, Long categoriaId) {
        if (produto.getId() != null && produtoRepository.existsById(produto.getId())) {
            throw new RegraNegocioException("ID do produto já existe");
        }
        Categoria categoria = categoriaRepository.findById(categoriaId)
            .orElseThrow(() -> new RegraNegocioException("Categoria não encontrada."));
        produto.setCategoria(categoria);
        if (produtoRepository.existsByNomeIgnoreCase(produto.getNome())) {
            throw new RegraNegocioException("Já existe um produto com esse nome.");
        }
        if (produto.getNome() != null && produto.getPreco() != null) {
            if (produto.getNome().toLowerCase().contains("promoção") && produto.getPreco() >= 500) {
                throw new RegraNegocioException("O preço de produtos em promoção deve ser menor que R$ 500,00.");
            }
        }
        return produtoRepository.save(produto);
    }

    public Produto atualizarProduto(Long id, Produto produto, Long categoriaId) {
        Produto existente = produtoRepository.findById(id)
            .orElseThrow(() -> new RegraNegocioException("Produto não encontrado."));
        Categoria categoria = categoriaRepository.findById(categoriaId)
            .orElseThrow(() -> new RegraNegocioException("Categoria não encontrada."));
        produto.setCategoria(categoria);
        if (produtoRepository.existsByNomeIgnoreCase(produto.getNome()) &&
            !produtoRepository.findById(id).map(p -> p.getNome().equalsIgnoreCase(produto.getNome())).orElse(false)) {
            throw new RegraNegocioException("Já existe um produto com esse nome.");
        }
        if (produto.getNome() != null && produto.getPreco() != null) {
            if (produto.getNome().toLowerCase().contains("promoção") && produto.getPreco() >= 500) {
                throw new RegraNegocioException("O preço de produtos em promoção deve ser menor que R$ 500,00.");
            }
        }
        produto.setId(id);
        return produtoRepository.save(produto);
    }

    public void deletarProduto(Long id) {
        if (!produtoRepository.existsById(id)) {
            throw new RegraNegocioException("Produto não encontrado.");
        }
        produtoRepository.deleteById(id);
    }

    public DescontoResponse aplicarDesconto(Long id, Double percentual) {
        Produto produto = produtoRepository.findById(id)
            .orElseThrow(() -> new RegraNegocioException("Produto não encontrado."));
        if (percentual < 0 || percentual > 50) {
            throw new RegraNegocioException("O percentual de desconto deve ser entre 0% e 50%.");
        }
        double precoOriginal = produto.getPreco();
        double precoFinal = precoOriginal * (1 - percentual / 100.0);
        String descontoAplicado = percentual + "%";
        return new DescontoResponse(produto.getNome(), precoOriginal, descontoAplicado, precoFinal);
    }
} 