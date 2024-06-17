package com.juanvictordev.megabyte.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "produtos")
public class Produto {
  
  //COLUNAS
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(nullable = false)
  String imagem;

  @Column(nullable = false)
  String nome;

  @Column(nullable = false, columnDefinition = "DECIMAL(8,2)")
  Double valor;

  @ManyToOne
  @JoinColumn(name = "id_categoria", nullable = false)
  Categoria categoria;
  
  @Column(nullable = false, columnDefinition = "TEXT")
  String descricao;

  //CONSTRUTOR
  public Produto(String imagem, String nome, Double valor, Categoria categoria, String descricao) {
    this.imagem = imagem;
    this.nome = nome;
    this.valor = valor;
    this.categoria = categoria;
    this.descricao = descricao;
  }

}
