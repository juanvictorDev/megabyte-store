package com.juanvictordev.megabyte.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.juanvictordev.megabyte.dto.FormDTO;
import com.juanvictordev.megabyte.entity.Categoria;
import com.juanvictordev.megabyte.entity.Produto;
import com.juanvictordev.megabyte.repository.ProdutoRepository;


@Service
public class ProdutoService {
  
  @Autowired
  ProdutoRepository produtoRepository;

  @Autowired
  MinioService minioService;
  //METODO PARA CRIAR PRODUTO
  public void criarProduto(FormDTO formDto){

    //LINKS DA IMAGEM E DESCRICAO
    String linkImg = formDto.nome().replaceAll("\\s+", "") + "-img";
    String linkDesc = formDto.nome().replaceAll("\\s+", "") + "-desc";

    //IMAGEM E DESCRICAO
    MultipartFile imagem = formDto.imagem();
    byte[] descricao = formDto.descricao().getBytes();

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
    
    //CRIANDO UMA LISTA E UM LOOP PARA SALVAR TODA A QUANTIDADE DE
    //PRODUTOS DE UMA VEZ NO BANCO
    List<Produto> listaDeProdutos = new ArrayList<>();
    
    for (int i = 0; i < formDto.quantidade(); i++) {
      Produto novoProduto = Produto.builder()
      .nome(formDto.nome())
      .valor(formDto.valor())
      .categoria(new Categoria(formDto.categoria()))
      .imagem(linkImg)
      .descricao(linkDesc)
      .build();

      listaDeProdutos.add(novoProduto);
    }
    
    produtoRepository.saveAll(listaDeProdutos);
  }




}