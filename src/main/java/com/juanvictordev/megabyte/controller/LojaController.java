package com.juanvictordev.megabyte.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import com.juanvictordev.megabyte.dto.ProdutoDTO;
import com.juanvictordev.megabyte.service.CategoriaService;
import com.juanvictordev.megabyte.service.MinioService;
import com.juanvictordev.megabyte.service.ProdutoService;


@Controller
public class LojaController {
  
  @Autowired
  CategoriaService categoriaService;

  @Autowired
  ProdutoService produtoService;

  @Autowired
  MinioService minioService;

  //ROTA PRINCIPAL PARA LISTAR OS PRODUTOS NA HOME COM FILTRAGEM DINAMICA OPCIONAL
  @GetMapping("/")
  public String paginaPrincipal(
    Model model,
    @PageableDefault(size = 10, page = 0) Pageable pageable, 
    @RequestParam(required = false) String nome,
    @RequestParam(required = false) Integer categoria
  ) {

    model.addAttribute("categorias", categoriaService.listarTodasCategorias());
    model.addAttribute("paginaDeProdutos", produtoService.listarTodosProdutosComFiltro(nome, categoria, pageable));
    return "index";
  }

  //ROTA ESPECIFICA PARA PRODUTO
  @GetMapping("/produto/{id}")
  public String getMethodName(@PathVariable Long id, Model model) {
    ProdutoDTO produto = produtoService.listarProduto(id);
    model.addAttribute("produto", produto);
    model.addAttribute("descricao", minioService.downloadDescricao(produto.getDescricao()));
    return "produto";
  }
  
}