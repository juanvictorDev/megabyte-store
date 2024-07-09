package com.juanvictordev.megabyte.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record FormCategoriaDTO(
  Integer id,

  @NotBlank
  @Size(min = 3, max = 45)
  String nome
  
) {
  public static FormCategoriaDTO empty(){
    return new FormCategoriaDTO(null, null);
  }
}