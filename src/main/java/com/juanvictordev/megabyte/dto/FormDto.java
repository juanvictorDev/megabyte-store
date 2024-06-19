package com.juanvictordev.megabyte.dto;

import org.springframework.web.multipart.MultipartFile;

public record FormDto(
  Long id, 
  String nome, 
  Integer categoria, 
  Integer quantidade,
  Double valor, 
  MultipartFile imagem, 
  String descricao
) {}