package br.com.fiap.mottuapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Páginas pós-login do usuário.
 * (Admin continua com o próprio controller/página /admin/home)
 */
@Controller
public class HomeController {

    /** Redireciona a raiz para /home */
    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    /** Home do usuário comum */
    @GetMapping("/home")
    public String homeUser() {
        return "home-user";
    }

    @GetMapping("/home-admin")
    public String legacyAdmin() {
        return "redirect:/admin";
    }

}
