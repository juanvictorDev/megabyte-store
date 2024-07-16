package com.juanvictordev.megabyte.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class HomeProdutoDTO {
  Long id;
  String nome;
  Double valor;
  String imagem;
}