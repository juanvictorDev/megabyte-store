package com.juanvictordev.megabyte.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.juanvictordev.megabyte.dto.CategoriaDTO;
import com.juanvictordev.megabyte.dto.FormCategoriaDTO;
import com.juanvictordev.megabyte.entity.Categoria;
import com.juanvictordev.megabyte.repository.CategoriaRepository;


@Service
public class CategoriaService {
 
  @Autowired
  CategoriaRepository categoriaRepository;
  
  public List<CategoriaDTO> listarTodasCategorias(){
    return categoriaRepository.seekAll();
  }

  public CategoriaDTO listarCategoria(Integer id){
    CategoriaDTO categoria = categoriaRepository.seekById(id)
    .orElseThrow(()-> new IllegalArgumentException());

    return categoria;
  }

  public void salvarCategoria(FormCategoriaDTO form){
    if(form.id() == null){
      categoriaRepository.save(new Categoria(form.nome()));
    }else{
      categoriaRepository.save(new Categoria(form.id(), form.nome()));
    }
  }

  
  public boolean validarCategoria(String nome){
    List<CategoriaDTO> categorias = listarTodasCategorias();
        
    for (CategoriaDTO categoria : categorias) {      
      if(nome.equals(categoria.getNome())){
        return false;
      }
    }
  
    return true;
  }
}