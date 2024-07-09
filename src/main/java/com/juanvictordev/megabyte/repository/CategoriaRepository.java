package com.juanvictordev.megabyte.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.juanvictordev.megabyte.dto.CategoriaDTO;
import com.juanvictordev.megabyte.entity.Categoria;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer>{
  
  //QUERY PARA TRAZER UMA CATEGORIA PELO ID
  @Query(value = "SELECT categorias.* FROM categorias WHERE id = :id", nativeQuery = true)
  public Optional<CategoriaDTO> seekById(@Param("id") Integer id);

  //QUERY PARA TRAZER TODOS CATEGORIAS
  @Query(value = "SELECT * FROM categorias ORDER BY id ASC", nativeQuery = true)
  public List<CategoriaDTO> seekAll();
}