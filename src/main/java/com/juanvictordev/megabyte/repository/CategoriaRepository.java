package com.juanvictordev.megabyte.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.juanvictordev.megabyte.entity.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer>{
  
}
