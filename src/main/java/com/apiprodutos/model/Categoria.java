package com.apiprodutos.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;

@Schema(description = "Categoria de produtos")
@Entity
@Table(name = "categoria")
public class Categoria {
    @Schema(description = "ID da categoria", example = "1")
    @Id
    private Long id;

    @Schema(description = "Nome da categoria", example = "Informática")
    private String nome;

    public Categoria() {}

    public Categoria(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
}