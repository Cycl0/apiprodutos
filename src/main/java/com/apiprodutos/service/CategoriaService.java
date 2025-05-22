package com.apiprodutos.service;

import com.apiprodutos.model.Categoria;
import com.apiprodutos.repository.CategoriaRepository;
import com.apiprodutos.repository.ProdutoRepository;
import com.apiprodutos.model.Produto;
import com.apiprodutos.exception.RegraNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Service
public class CategoriaService {
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Categoria> listarTodas() {
        return categoriaRepository.findAll();
    }

    public Categoria buscarPorId(Long id) {
        return categoriaRepository.findById(id)
            .orElseThrow(() -> new RegraNegocioException("Categoria não encontrada."));
    }

    public List<Categoria> buscarPorNome(String nome) {
        return categoriaRepository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Produto> listarProdutosPorCategoria(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
            .orElseThrow(() -> new RegraNegocioException("Categoria não encontrada."));
        return produtoRepository.findByCategoriaId(id);
    }

    public Categoria criarCategoria(Categoria categoria) {
        if (categoria.getId() != null && categoriaRepository.existsById(categoria.getId())) {
            throw new RegraNegocioException("ID da categoria já existe");
        }
        if (categoriaRepository.existsByNomeIgnoreCase(categoria.getNome())) {
            throw new RegraNegocioException("Já existe uma categoria com esse nome.");
        }
        return categoriaRepository.save(categoria);
    }

    public Categoria atualizarCategoria(Long id, Categoria categoria) {
        Categoria categoriaExistente = categoriaRepository.findById(id)
            .orElseThrow(() -> new RegraNegocioException("Categoria não encontrada."));
        categoriaExistente.setNome(categoria.getNome());
        return categoriaRepository.save(categoriaExistente);
    }

    public void deletarCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new RegraNegocioException("Categoria não encontrada.");
        }
        categoriaRepository.deleteById(id);
    }
} 