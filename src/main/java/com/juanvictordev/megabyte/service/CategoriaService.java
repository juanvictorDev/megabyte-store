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
  
  //METODO PARA LISTAR TODAS AS CATEGORIAS
  public List<CategoriaDTO> listarTodasCategorias(){
    return categoriaRepository.seekAll();
  }

  //METODO PARA LISTAR UMA CATEGORIA
  public CategoriaDTO listarCategoria(Integer id){
    CategoriaDTO categoria = categoriaRepository.seekById(id)
    .orElseThrow(()-> new IllegalArgumentException());

    return categoria;
  }

  //METODO PARA SALVAR CATEGORIA NO BANCO
  public void salvarCategoria(FormCategoriaDTO form){
    if(form.id() == null){
      categoriaRepository.save(new Categoria(form.nome()));
    }else{
      categoriaRepository.save(new Categoria(form.id(), form.nome()));
    }
  }

  //METODO UTILITARIO PARA VALIDAR SE A CATEGORIA JA EXISTE NO BANCO
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