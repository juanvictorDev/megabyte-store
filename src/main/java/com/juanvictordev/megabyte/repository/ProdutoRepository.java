package com.juanvictordev.megabyte.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.juanvictordev.megabyte.dto.EditFormDTO;
import com.juanvictordev.megabyte.entity.Produto;


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
  Optional<EditFormDTO> findByIdWithCount(@Param("id") Long id);
}