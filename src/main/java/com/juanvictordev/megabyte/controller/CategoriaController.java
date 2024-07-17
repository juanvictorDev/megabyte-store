package com.juanvictordev.megabyte.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.juanvictordev.megabyte.dto.CategoriaDTO;
import com.juanvictordev.megabyte.dto.FormCategoriaDTO;
import com.juanvictordev.megabyte.service.CategoriaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@RequestMapping("adm")
public class CategoriaController {
  
  @Autowired
  CategoriaService categoriaService;

  //ROTA DUPLA PARA CRIAR E EDITAR UMA CATEGORIA
  @GetMapping( value = {"/criar-categoria", "/editar-categoria/{id}"})
  public String formularioCategoria(Model model, @PathVariable(required = false) Integer id) {
    
    if(!model.containsAttribute("form")){
      model.addAttribute("form", FormCategoriaDTO.empty());
    }

    if(id != null){
      model.addAttribute("categoria", categoriaService.listarCategoria(id));
    }
    
    return "formularioCategoria";
  }
  
  //ROTA PARA SALVAR A CATEGORIA
  @PostMapping("/salvar-categoria")
  public String salvarCategoria(@Valid @ModelAttribute FormCategoriaDTO form, BindingResult result, RedirectAttributes redirect) {
    
    //METODO UTILITARIO PARA VALIDADE SE O NOME DA CATEGORIA JA EXISTE NO BANCO
    //SE EXISTIR CRIAR UM ERRO NO CAMPO
    if(!categoriaService.validarCategoria(form.nome())){
      result.addError(new FieldError("form", "nome", "categoria de produtos já existe"));
    }

    //SE EXISTIR ERROS REDIRECIONAR PARA O FORMULARIO NOVAMENTE
    if(result.hasErrors()){
      redirect.addFlashAttribute("org.springframework.validation.BindingResult.form", result);
      redirect.addFlashAttribute("form", form);
      return "redirect:/" + (form.id() != null ? "adm/editar-categoria/" + form.id() : "adm/criar-categoria");
    }
    
    categoriaService.salvarCategoria(form);

    return "redirect:/adm/gerenciar-categoria";
  }
  
  //ROTA PARA LISTAR TODAS AS CATEGORIAS
  @GetMapping("/gerenciar-categoria")
  public String gerenciarCategorias(Model model){
    model.addAttribute("categorias", categoriaService.listarTodasCategorias());
    return "gerenciarCategoria";
  }
}
