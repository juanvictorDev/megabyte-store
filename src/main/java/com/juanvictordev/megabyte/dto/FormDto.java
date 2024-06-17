package com.juanvictordev.megabyte.dto;

import org.springframework.web.multipart.MultipartFile;

public record FormDto(
  Long id, String nome, String categoria, Integer quantidade,
  Double preco, MultipartFile imagem, String descricao
) {}