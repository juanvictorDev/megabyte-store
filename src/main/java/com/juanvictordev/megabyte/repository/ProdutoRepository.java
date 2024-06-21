package com.juanvictordev.megabyte.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.juanvictordev.megabyte.dto.ProdutoDTO;
import com.juanvictordev.megabyte.entity.Produto;
import jakarta.transaction.Transactional;


@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long>{
  
  @Query( value = 
    "SELECT p.*, count_table.total_count as quantidade " +
    "FROM produtos p " +
    "JOIN ( " +
      "SELECT nome, COUNT(*) AS total_count " +
      "FROM produtos " +
      "WHERE nome = (SELECT nome FROM produtos WHERE id = :id) " +
      "GROUP BY nome " +
      ") AS count_table ON p.nome = count_table.nome " +
      "WHERE p.id = :id ",
      nativeQuery = true
    )
  Optional<ProdutoDTO> findByIdWithCount(@Param("id") Long id);
      
  @Modifying
  @Transactional
  @Query(value = 
    "UPDATE produtos p SET " +
    "p.nome = :nome, " +
    "p.valor = :valor, " +
    "p.id_categoria = :categoria, " +
    "p.imagem = :imagem, " +
    "p.descricao = :descricao " +
    "WHERE p.nome = :nomeOriginal", nativeQuery = true)
  Integer updateProdutoByNome(
    @Param("nome") String nome,
    @Param("valor") Double valor,
    @Param("categoria") Integer categoria,
    @Param("imagem") String imagem,
    @Param("descricao") String descricao,
    @Param("nomeOriginal") String nomeOriginal
  );

  @Modifying
  @Transactional
  @Query(value = 
    "DELETE FROM produtos WHERE nome = :nome LIMIT :quantidade",
    nativeQuery = true)
  void deleteProdutoByNomeWithLimit(@Param("nome") String nome, @Param("quantidade") Integer quantidade);

}