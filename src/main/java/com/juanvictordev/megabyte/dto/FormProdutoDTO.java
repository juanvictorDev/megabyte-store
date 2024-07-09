package com.juanvictordev.megabyte.dto;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record FormProdutoDTO(
  Long id, 
  
  @NotBlank
  @Size(min = 2, max = 255)
  String nome, 
  
  @NotNull
  Integer categoria, 
  
  @Min(value = 1)
  @Max(value = 20)
  Integer quantidade,
  
  @Min(value = 1)
  @Max(value = 1000000)
  Double valor, 

  MultipartFile imagem, 
  
  @NotBlank
  String descricao

) {

  public static FormProdutoDTO empty(){
    return new FormProdutoDTO(null, null, null, null, null, null, null);
  }
}