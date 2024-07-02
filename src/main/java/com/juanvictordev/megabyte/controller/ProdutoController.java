package com.juanvictordev.megabyte.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import com.juanvictordev.megabyte.dto.FormDTO;
import com.juanvictordev.megabyte.entity.Categoria;
import com.juanvictordev.megabyte.repository.CategoriaRepository;
import com.juanvictordev.megabyte.service.MinioService;
import com.juanvictordev.megabyte.service.ProdutoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;



@Controller
public class ProdutoController {

  @Autowired
  ProdutoService produtoService;
  
  // arrumar dps
  @Autowired
  CategoriaRepository categoriaRepository;


  @Autowired
  MinioService minioService;

  //ROTA PARA CRIAR E EDITAR, JA QUE POSSUEM LOGICAS PARECIDAS
  //REAPROVEITAR O MESMO FORMULARIO
  @GetMapping(value = {"/criar-produto", "/editar-produto/{id}"})
  public String formularioProduto(Model model, @PathVariable(required = false) Long id){
    //SE O FORMULARIO POSSUI ERRO, SERA REDIRECIANADO PARA ESSE CONTROLLER
    //QUE VAI IGNORAR ESSE BLOCO SE JA POSSUIR UM FORM REDIRECIONADO DO /SALVAR
    //SE NAO, CRIAR UM NOVO PARA SERVIR DE MODELO PARA O FORM
    if(!model.containsAttribute("form")){
      model.addAttribute("form", FormDTO.empty());
    }
    //METODO PARA ADICIONAR OS DADOS NECESSARIOS, PARA CRIACAO OU EDICAO
    model.addAllAttributes(produtoService.dadosParaFormulario(id));
    
    return "formularioProduto";
  }

  //ROTA PARA LIDAR COM A VALIDACAO DO FORMULARIO, CRIACAO E EDICAO DO PRODUTO
  @PostMapping("/salvar-produto")
  public String salvarProduto(@Valid @ModelAttribute("form") FormDTO form, BindingResult result, RedirectAttributes redirectAttributes, HttpServletRequest request){ 
    //SE IMAGEM NAO FOR DE UM TIPO MIME VALIDO, CRIAR UM NOVO ERRO 
    if(!produtoService.validarImagem(form.imagem(), request)){
      result.addError(new FieldError("form", "imagem", "selecione uma imagem válida (jpeg, png, webp)"));
    }

    //SE O FORMULARIO POSSUIR ERRO, ADICIONAR TODOS OS ATRIBUTOS E REDIRECIONAR PARA O CONTROLLER APROPRIADO
    if(result.hasErrors()){
      redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.form", result);
      redirectAttributes.addFlashAttribute("form", form);
      return "redirect:/" + (form.id() != null ? "editar-produto/" + form.id() : "criar-produto");
    }

    if(form.id() == null){
      produtoService.criarProduto(form);
    }else{
      produtoService.editarProduto(form);
    }

    return "redirect:/gerenciar-produto";
  }

  @GetMapping("/gerenciar-produto")
  public String gerenciarProduto(Model model) {
    model.addAttribute("paginaDeProdutos", produtoService.listarTodosProdutos());
    return "gerenciarProduto";
  }
  
  @PostMapping("/deletar-produto")
  public String deletarProduto(@RequestParam String nome, @RequestParam Integer quantidade) {
    produtoService.deletarProduto(nome, quantidade);
    return "redirect:/gerenciar-produto";
  }
  
}