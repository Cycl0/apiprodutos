package com.apiprodutos.dto;

public class DescontoResponse {
    private String nome;
    private Double precoOriginal;
    private String descontoAplicado;
    private Double precoFinal;

    public DescontoResponse(String nome, Double precoOriginal, String descontoAplicado, Double precoFinal) {
        this.nome = nome;
        this.precoOriginal = precoOriginal;
        this.descontoAplicado = descontoAplicado;
        this.precoFinal = precoFinal;
    }
    
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public Double getPrecoOriginal() { return precoOriginal; }
    public void setPrecoOriginal(Double precoOriginal) { this.precoOriginal = precoOriginal; }
    public String getDescontoAplicado() { return descontoAplicado; }
    public void setDescontoAplicado(String descontoAplicado) { this.descontoAplicado = descontoAplicado; }
    public Double getPrecoFinal() { return precoFinal; }
    public void setPrecoFinal(Double precoFinal) { this.precoFinal = precoFinal; }
}