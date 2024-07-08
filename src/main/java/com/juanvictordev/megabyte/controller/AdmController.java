package com.juanvictordev.megabyte.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class AdmController {
  
  @GetMapping("/adm")
  public String admView() {
    return "administrador";
  }
  
}
