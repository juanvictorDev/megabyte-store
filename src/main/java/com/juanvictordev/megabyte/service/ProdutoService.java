package com.juanvictordev.megabyte.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.juanvictordev.megabyte.dto.ProdutoDTO;
import com.juanvictordev.megabyte.dto.FormDTO;
import com.juanvictordev.megabyte.entity.Categoria;
import com.juanvictordev.megabyte.entity.Produto;
import com.juanvictordev.megabyte.repository.CategoriaRepository;
import com.juanvictordev.megabyte.repository.ProdutoRepository;

import jakarta.servlet.http.HttpServletRequest;


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
    produtoRepository.updateByNome(
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
      produtoRepository.deleteByNomeWithLimit(form.nome(), calculoQuantidade);
    }
  } 

  //METODO PARA TRAZER TODOS OS PRODUTOS DE FORMA PAGINADA
  public Page<List<ProdutoDTO>> listarTodosProdutos(Pageable pageable){
    return produtoRepository.findAllWithCount(pageable);
  }

  //METODO PARA DELETAR PRODUTO PELO NOME, A PARTE DA QUANTIDADE
  //ESTA SENDO REAPROVEITADA, PORQUE USA A MESMA QUERY PARA REMOVER PRODUTOS
  //DENTRO DO METODO DE ATUALIZAR, AQUI NO CASO SEMPRE EH PASSADO A QUANTIDADE 
  //TOTAL CONSEQUENTEMENTE EXCLUINDO TODOS OS PRODUTOS
  public void deletarProduto(String nome, Integer quantidade){
    produtoRepository.deleteByNomeWithLimit(nome, quantidade);

    //deletar a img e a desc no min.io
  }



  //--METODOS UTILITARIOS--
  
  //METODO PARA PADRONIZAR OS DADOS E ENVIAR PARA O UPLOAD DO MIN.IO E RETORNAR OS LINKS
  //POSSUI O FLUXO DE CRIACAO, QUANDO NAO EH PASSADO O PRODUTO ORIGINAL DO BANCO
  //POSSUI FLUXO DE EDICAO QUANDO EH PASSADO O PRODUTO ORIGINAL DO BANCO
  private Map<String, String> enviarPadraoMinio(FormDTO form, ProdutoDTO produtoOriginal){
    
    //EDICAO DE PRODUTO COM NOVA IMAGEM
    if(form.id() != null && !form.imagem().isEmpty()){
      //METODO PARA DELETAR OS OBJETOS QUE SERAO SUBSTITUIDOS PELOS NOVOS NO MIN.IO
      //SE O PRODUTO FOR ENVIADO COM NOVO NOME, NO MIN.IO VAI EXISTIR 2, O ANTIGO E O NOVO
      //POR ISSO EXCLUIR TUDO ANTES, PRA GARANTIR A INTEGRIDADE, PQ SE FOR O MSM NOME
      //O MIN.IO VAI FAZER O REPLACE, MAS SE FOR NOME DIFERENTE NAO 
      minioService.deleteDados(produtoOriginal.getImagem(), produtoOriginal.getDescricao());
    }

    //PADRAO DE LINKS DA IMAGEM E DESCRICAO
    String linkImg = form.nome().replaceAll("\\s+", "") + "-img";
    String linkDesc = form.nome().replaceAll("\\s+", "") + "-desc";

    //EDICAO DE PRODUTO COM A MSM IMAGEM
    if(form.id() != null && form.imagem().isEmpty()){
      //QUANDO ALTERA O NOME DO PRODUTO
      if(!form.nome().equals(produtoOriginal.getNome())){
        minioService.renameObject(produtoOriginal.getImagem(), linkImg);
        minioService.deleteDados(produtoOriginal.getImagem(), produtoOriginal.getDescricao());
      }
      //DESCRICAO SEMPRE EH CRIADA NOVAMENTE E COM O PADRAO DE NOME
      //PARA GARANTIR A INTEGRIDADE DO BANCO E MIN.IO
      byte[] descricao = form.descricao().getBytes();
      
      //FAZENDO O UPLOAD DA DESCRICAO NO MIN.IO
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

  //METODO PARA VALIDAR A IMAGEM ENVIADA
  public boolean validarImagem(MultipartFile file, HttpServletRequest request){
    String tipo = file.getContentType();
    String referer = request.getHeader("Referer");
    boolean rotaEditar = referer != null && referer.contains("/editar-produto/");
    
    //FLUXO DE EDIÇÃO SE O USER QUISER UTILIZAR A MESMA IMAGEM
    //NÃO PRECISA PASSAR UMA NOVA, CONSQUENTEMENTE RETORNANDO TRUE
    //CASO FOR FLUXO DE CRIAÇÃO NÃO É POSSIVEL ENVIAR IMAGEM NULL
    //VALIDADO NO HMTL COM REQUIRED
    if(rotaEditar && file.isEmpty()){
      return true;
    }

    return 
      tipo.equals("image/png") || 
      tipo.equals("image/jpeg") || 
      tipo.equals("image/webp");
  }

}