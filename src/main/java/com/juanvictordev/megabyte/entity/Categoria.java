package com.juanvictordev.megabyte.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "categorias")
public class Categoria {
  
  //COLUNAS
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Integer id;

  @Column(nullable = false, unique = true)
  String nome;

  @OneToMany(mappedBy = "categoria")
  List<Produto> produtos;

  //CONSTRUTOR
  public Categoria(String nome) {
    this.nome = nome;
  }

  public Categoria(Integer id) {
    this.id = id;
  }
  
  public Categoria(Integer id, String nome) {
    this.id = id;
    this.nome = nome;
  }
}
