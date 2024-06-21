package com.juanvictordev.megabyte.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.juanvictordev.megabyte.dto.ProdutoDTO;
import com.juanvictordev.megabyte.dto.FormDTO;
import com.juanvictordev.megabyte.entity.Categoria;
import com.juanvictordev.megabyte.entity.Produto;
import com.juanvictordev.megabyte.repository.CategoriaRepository;
import com.juanvictordev.megabyte.repository.ProdutoRepository;


@Service
public class ProdutoService {
  
  @Autowired
  ProdutoRepository produtoRepository;

  @Autowired
  CategoriaRepository categoriaRepository;

  @Autowired
  MinioService minioService;

  //METODO PARA CRIAR PRODUTO
  public void criarProduto(FormDTO form){

    //METODO UTILITARIO PARA ENVIAR O PADRAO DE DADOS PARA O MIN.IO E RETORNAR OS LINKS
    //  ve a questão do input hiddem lá no form
    //**************** explicar a logica dps ******************************
    //
    Map<String, String> links = enviarPadraoMinio(form, null);
    
    //CRIANDO UMA LISTA E UM LOOP PARA SALVAR TODA A QUANTIDADE DE
    //PRODUTOS DE UMA VEZ NO BANCO
    List<Produto> listaDeProdutos = new ArrayList<>();
    
    for (int i = 0; i < form.quantidade(); i++) {
      Produto novoProduto = Produto.builder()
      .nome(form.nome())
      .valor(form.valor())
      .categoria(new Categoria(form.categoria()))
      .imagem(links.get("imagem"))
      .descricao(links.get("descricao"))
      .build();

      listaDeProdutos.add(novoProduto);
    }
    //SALVAR TODOS OS PRODUTOS
    produtoRepository.saveAll(listaDeProdutos);
  }


  //METODO PARA EDITAR PRODUTO
  public void editarProduto(FormDTO form){
    
    //BUSCANDO PRODUTO ORIGINAL DO BANCO
    ProdutoDTO produtoOriginal = produtoRepository.findByIdWithCount(form.id())
    .orElseThrow(() -> new IllegalArgumentException());

    //METODO UTILITARIO PARA ENVIAR O PADRAO DE DADOS PARA O MIN.IO E RETORNAR OS LINKS
    Map<String, String> links = enviarPadraoMinio(form, produtoOriginal);

    //CALCULO PARA COMPARAR A QUANTIDADE DE PRODUTOS DO FORMULARIO VS DO BANCO
    int calculoQuantidade = produtoOriginal.getQuantidade() - form.quantidade();

    //METODO PARA ATUALIZAR OS TODOS OS PRODUTOS PELO NOME
    produtoRepository.updateProdutoByNome(
      form.nome(),
      form.valor(),
      form.categoria(),
      links.get("imagem"),
      links.get("descricao"),
      produtoOriginal.getNome()
    );

    //SE A QUANTIDADE FINAL FOR NEGATIVA, ADICIONAR O RESTANTE
    if(calculoQuantidade < 0){
      List<Produto> listaDeProdutos = new ArrayList<>();
      for (int i = 0; i < Math.abs(calculoQuantidade); i++) { 
        Produto novoProduto = Produto.builder()
        .nome(form.nome())
        .valor(form.valor())
        .categoria(new Categoria(form.categoria()))
        .imagem(links.get("imagem"))
        .descricao(links.get("descricao"))
        .build();
        
        listaDeProdutos.add(novoProduto);
      }

      produtoRepository.saveAll(listaDeProdutos);
    
    //SE A QUANTIDADE FINAL FOR POSITIVA, DELETAR O RESTANTE
    }else if(calculoQuantidade > 0){
      produtoRepository.deleteProdutoByNomeWithLimit(form.nome(), calculoQuantidade);
    }
  } 


  //METODOS UTILITARIOS 
  
  //METODO PARA PADRONIZAR OS DADOS E ENVIAR PARA O UPLOAD DO MIN.IO
  private Map<String, String> enviarPadraoMinio(FormDTO form, ProdutoDTO produtoOriginal){
    
    //ve oq fazer dps imagem diferente
    if(form.id() != null && !form.imagem().isEmpty()){
      System.out.println("METODO COM IMAGEM NOVA");
      //METODO PARA DELETAR OS OBJETOS QUE SERAO SUBSTITUIDOS NO MIN.IO
      minioService.deleteDados(produtoOriginal.getImagem(), produtoOriginal.getDescricao());
    }

    //LINKS DA IMAGEM E DESCRICAO
    String linkImg = form.nome().replaceAll("\\s+", "") + "-img";
    String linkDesc = form.nome().replaceAll("\\s+", "") + "-desc";

    //msm imagem
    if(form.id() != null && form.imagem().isEmpty()){
      System.out.println("METODO COM A MESMA IMAGEM");
      
      
      minioService.renameObject(produtoOriginal.getImagem(), linkImg);
      minioService.deleteDados(produtoOriginal.getImagem(), produtoOriginal.getDescricao());
      

      byte[] descricao = form.descricao().getBytes();
      
      try (
        ByteArrayInputStream descricaoStream = new ByteArrayInputStream(descricao);
      ){
        minioService.upload(descricaoStream, descricao.length, linkDesc);
      } catch (Exception e) {
        e.printStackTrace();
      }

      return Map.of("imagem", linkImg, "descricao", linkDesc);
    }


    //IMAGEM E DESCRICAO
    MultipartFile imagem = form.imagem();
    byte[] descricao = form.descricao().getBytes();

    //CRIANDO OS INPUT STREAMS DA IMAGEM E DESCRICAO PARA O PARAMETRO
    //DO METODO UPLOAD PARA SALVAR NO MIN.IO
    try (
      InputStream imagemStream = imagem.getInputStream();
      ByteArrayInputStream descricaoStream = new ByteArrayInputStream(descricao);
    ){
      minioService.upload(imagemStream, imagem.getSize(), linkImg);
      minioService.upload(descricaoStream, descricao.length, linkDesc);
    } catch (Exception e) {
      e.printStackTrace();
    }

    //RETORNANDO OS NOMES DOS OBJETOS DO MIN.IO PARA INSERIR NO BANCO
    return Map.of("imagem", linkImg, "descricao", linkDesc);
  }


  //METODO PARA ENVIAR OS DADOS NECESSARIOS PARA O FORMULARIO DE CRIACAO E EDICAO
  public Map<String, Object> dadosParaFormulario(Long id){
    Map<String, Object> dados = new HashMap<>();
    
    dados.put("categorias", categoriaRepository.findAll());
    
    if(id != null){
      ProdutoDTO produto = produtoRepository.findByIdWithCount(id)
      .orElseThrow(() -> new IllegalArgumentException());
      
      String descricao = minioService.downloadDescricao(produto.getDescricao());
      
      dados.put("produto", produto);
      dados.put("descricao", descricao);  
    }

    return dados;
  }

}