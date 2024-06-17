package com.juanvictordev.megabyte.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;

//CONTROLLER PARA GERENCIAR AS PAGINAS DE ERRO
@Controller
public class AppErrorController implements ErrorController{
  
  @RequestMapping("/error")
  public String errorHandler(HttpServletRequest request, Model model){

    Integer status = Integer.parseInt(request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE).toString());

    switch (status) {
      case 400:
        model.addAttribute("code", status);
        model.addAttribute("msg", "Bad Request");
        break;
      case 401:
        model.addAttribute("code", status);
        model.addAttribute("msg", "Unauthorized");
        break;
      case 404:
        model.addAttribute("code", status);
        model.addAttribute("msg", "Resource not found");
        break;
      case 500:
        model.addAttribute("code", status);
        model.addAttribute("msg", "Internal Server Error");
        break;
      default:
        model.addAttribute("code", status);
        break;
    }

    return "error";
  }
}

