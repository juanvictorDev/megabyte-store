package com.juanvictordev.megabyte.dto;

public interface ProdutoDTO {
  Long getId();
  String getNome();
  Integer getId_categoria();
  String getNome_categoria();
  Integer getQuantidade();
  Double getValor();
  String getImagem();
  String getDescricao();
}
