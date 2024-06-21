package com.juanvictordev.megabyte.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.juanvictordev.megabyte.dto.FormDTO;
import com.juanvictordev.megabyte.service.MinioService;
import com.juanvictordev.megabyte.service.ProdutoService;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class ProdutoController {

  @Autowired
  ProdutoService produtoService;
  
  @Autowired
  MinioService minioService;


  @GetMapping(value = {"/criar", "/editar/{id}"})
  public String formulario(Model model, @PathVariable(required = false) Long id){

    model.addAllAttributes(produtoService.dadosParaFormulario(id));
    
    return "form";
  }


  @PostMapping("/salvar")
  public String salvar(FormDTO form){
  
    if(form.id() == null){
      produtoService.criarProduto(form);
    }else{
      produtoService.editarProduto(form);
    }

    return "home";
  }


  // @GetMapping("/test")
  // public String getMethodName(Model model) throws Exception {
  //   Produto produto = produtoRepository.findById(30L).get();
  //   model.addAttribute("produto", produto);
  //   model.addAttribute("descricao", minioService.download(produto.getDescricao()));
  //   return "test";
  // }
  
}