package com.juanvictordev.megabyte.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.juanvictordev.megabyte.entity.Produto;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, Long>{
  
}