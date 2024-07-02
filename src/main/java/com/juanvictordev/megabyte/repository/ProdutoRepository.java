package com.juanvictordev.megabyte.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
  
  //SELECT PARA TRAZER O PRODUTO COM A QUANTIDADE QUE POSSUI NO BANCO
  @Query( value = 
    "SELECT p.*, count_table.total_count as quantidade " +
    "FROM produtos p " +
    "JOIN ( " +
      "SELECT nome, COUNT(*) AS total_count FROM produtos " +
      "WHERE nome = (SELECT nome FROM produtos WHERE id = :id) " +
      "GROUP BY nome " +
    ") AS count_table ON p.nome = count_table.nome WHERE p.id = :id ",
    nativeQuery = true
  )
  Optional<ProdutoDTO> findByIdWithCount(@Param("id") Long id);


  //QUERY PARA TRAZER TODOS OS PRODUTOS COM A SUA QUANTIDADE NO BANCO
  //DE FORMA PAGINADA
  @Query(value = 
  "SELECT p.*, c.nome as nome_categoria, sub.total_count as quantidade " +
  "FROM produtos p " +
  "JOIN categorias c ON p.id_categoria = c.id " +
  "JOIN ( " +
    "SELECT nome, COUNT(*) as total_count, MIN(id) as min_id " +
    "FROM produtos " +
    "GROUP BY nome " +
  ") sub ON p.nome = sub.nome AND p.id = sub.min_id",
  countQuery = "SELECT COUNT(DISTINCT nome) FROM produtos",
  nativeQuery = true
)
Page<List<ProdutoDTO>> findAllWithCount(Pageable pageable);


  
  //QUERY PARA ATUALIZAR TODOS OS PRODUTOS COM BASE NO NOME
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
  Integer updateByNome(
    @Param("nome") String nome,
    @Param("valor") Double valor,
    @Param("categoria") Integer categoria,
    @Param("imagem") String imagem,
    @Param("descricao") String descricao,
    @Param("nomeOriginal") String nomeOriginal
  );


  //QUERY PARA DELETAR "X" PRODUTOS COM O MESMO NOME 
  @Modifying
  @Transactional
  @Query(value = 
    "DELETE FROM produtos WHERE nome = :nome LIMIT :quantidade",
    nativeQuery = true)
  void deleteByNomeWithLimit(@Param("nome") String nome, @Param("quantidade") Integer quantidade);

}