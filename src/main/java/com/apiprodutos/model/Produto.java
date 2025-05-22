package com.apiprodutos.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.DecimalMax;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Schema(description = "Produto cadastrado no sistema")
@Entity
@Table(name = "produto")
public class Produto {
    @Schema(description = "ID do produto", example = "1")
    @Id
    private Long id;

    @NotBlank(message = "O nome do produto é obrigatório")
    @Size(min = 2, max = 150, message = "O nome do produto deve conter entre 2 e 150 caracteres")
    @Schema(description = "Nome do produto", example = "Notebook")
    private String nome;

    @DecimalMax(value = "10000.00", message = "O preço do produto deve ser maior ou igual a R$ 10.000,00")
    @Schema(description = "Preço do produto", example = "3500.0")
    private Double preco;

    @Schema(description = "Categoria do produto")
    @ManyToOne(optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private Categoria categoria;

    public Produto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Double getPreco() { return preco; }
    public void setPreco(Double preco) { this.preco = preco; }
    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }
}