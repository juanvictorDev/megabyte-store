package com.juanvictordev.megabyte.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.juanvictordev.megabyte.dto.EditFormDTO;
import com.juanvictordev.megabyte.dto.FormDTO;
import com.juanvictordev.megabyte.entity.Produto;
import com.juanvictordev.megabyte.repository.CategoriaRepository;
import com.juanvictordev.megabyte.repository.ProdutoRepository;
import com.juanvictordev.megabyte.service.MinioService;
import com.juanvictordev.megabyte.service.ProdutoService;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class ProdutoController {

  @Autowired
  ProdutoRepository produtoRepository;
  
  @Autowired
  ProdutoService produtoService;
  
  //excluir dps
  @Autowired
  MinioService minioService;

  @Autowired
  CategoriaRepository categoriaRepository;

  @GetMapping(value = {"/criar", "/editar/{id}"})
  public String form(Model model, @PathVariable(required = false) Long id){
    model.addAttribute("categorias", categoriaRepository.findAll());
    
    if(id == null){
      return "form";
    }else{
      EditFormDTO produto = produtoRepository.findByIdWithCount(id)
      .orElseThrow(() -> new IllegalArgumentException());
      
      String descricao = minioService.download(produto.getDescricao());
      
      model.addAttribute("produto", produto);
      model.addAttribute("descricao", descricao);
      
      return "form";   
    }
  }


  @PostMapping("/salvar")
  public String salvar(FormDTO formDto){

    if(formDto.id() == null){
      produtoService.criarProduto(formDto);
    }else{
      produtoService.editarProduto(formDto);
    }

    return "home";
  }


  @GetMapping("/test")
  public String getMethodName(Model model) throws Exception {
    Produto produto = produtoRepository.findById(30L).get();
    model.addAttribute("produto", produto);
    model.addAttribute("descricao", minioService.download(produto.getDescricao()));
    return "test";
  }
  
}