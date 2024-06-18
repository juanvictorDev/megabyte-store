package com.juanvictordev.megabyte.service;

import java.io.ByteArrayInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.juanvictordev.megabyte.dto.FormDto;
import com.juanvictordev.megabyte.entity.Categoria;
import com.juanvictordev.megabyte.entity.Produto;
import com.juanvictordev.megabyte.repository.ProdutoRepository;

@Service
public class ProdutoService {
  
  @Autowired
  ProdutoRepository produtoRepository;

  @Autowired
  MinioService minioService;

  public String fluxoAdd(FormDto formDto) throws Exception{

    MultipartFile imagem = formDto.imagem();
    minioService.upload(
      imagem.getInputStream(),
      imagem.getSize(),
      formDto.nome().replaceAll("\\s+", "") + "-img"
    );

    String descricao = formDto.descricao();
    minioService.upload(
      new ByteArrayInputStream(descricao.getBytes()),
      descricao.getBytes().length,
      formDto.nome().replaceAll("\\s+", "") + "-desc"
    );

    Categoria categoria = new Categoria();
    categoria.setId(1);
    
    produtoRepository.save(new Produto(
      formDto.nome().replaceAll("\\s+", "") + "-img",
      formDto.nome(),
      formDto.preco(),
      categoria,
      formDto.nome().replaceAll("\\s+", "") + "-desc"
    ));

    return null;
  }
}
