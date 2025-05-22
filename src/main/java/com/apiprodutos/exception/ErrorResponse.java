package com.apiprodutos.exception;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String erro;
    private List<String> mensagens;
    private String caminho;

    // Getters e setters
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
    public String getErro() { return erro; }
    public void setErro(String erro) { this.erro = erro; }
    public List<String> getMensagens() { return mensagens; }
    public void setMensagens(List<String> mensagens) { this.mensagens = mensagens; }
    public String getCaminho() { return caminho; }
    public void setCaminho(String caminho) { this.caminho = caminho; }
}
