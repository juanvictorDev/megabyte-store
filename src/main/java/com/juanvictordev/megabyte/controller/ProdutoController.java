package com.juanvictordev.megabyte.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import com.juanvictordev.megabyte.dto.FormDto;
import com.juanvictordev.megabyte.entity.Produto;
import com.juanvictordev.megabyte.repository.ProdutoRepository;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
public class ProdutoController {

  @Autowired
  ProdutoRepository produtoRepository;

  @GetMapping(value = {"/criar", "/editar/{id}"})
  public String form(Model model, @PathVariable(required = false) Long id){
      
    if(id == null){
      return "form";
    }else{
      Produto produto = produtoRepository.findById(id).orElseThrow(() -> new IllegalArgumentException());
      model.addAttribute("produto", produto);
      return "form";   
    }
  }

  @PostMapping("/salvar")
  public String salvar(@ModelAttribute("formDto") FormDto formDto) {

    
      
    return null;
  }
}
