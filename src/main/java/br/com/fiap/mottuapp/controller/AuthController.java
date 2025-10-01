package br.com.fiap.mottuapp.controller;

import br.com.fiap.mottuapp.model.Usuario;
import br.com.fiap.mottuapp.repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

  private final UsuarioRepository usuarioRepo;
  private final PasswordEncoder passwordEncoder;

  @GetMapping("/login")
  public String login(@RequestParam(value="registered", required=false) String registered,
                      @RequestParam(value="error", required=false) String error,
                      Model model) {
    if (registered != null) model.addAttribute("success", "Cadastro realizado! Faça login.");
    if (error != null) model.addAttribute("error", "Usuário ou senha inválidos.");
    return "auth/login";
  }

  /* ===== Registro ===== */
  @GetMapping("/register")
  public String registerForm(Model model){
    model.addAttribute("usuario", new Usuario()); // <-- evita o erro do template
    return "auth/register";
  }

  @PostMapping("/register")
  public String registerSubmit(@Valid @ModelAttribute("usuario") Usuario usuario, BindingResult result, Model model){
    // validações simples
    if (usuario.getUsername() != null && usuarioRepo.existsByUsername(usuario.getUsername())) {
      result.rejectValue("username","exists","Usuário já existe");
    }
    if (usuario.getEmail() != null && usuarioRepo.existsByEmail(usuario.getEmail())) {
      result.rejectValue("email","exists","E-mail já existe");
    }
    if (result.hasErrors()) {
      return "auth/register";
    }

    // ativa usuário + criptografa a senha
    usuario.setAtivo(true);
    usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
    // perfil: se o seu enum/coluna exigir algo específico, set aqui (ex.: usuario.setPerfil(Perfil.USER))
    usuarioRepo.save(usuario);

    return "redirect:/login?registered";
  }

  @GetMapping("/pos-login")
  public String posLogin(org.springframework.security.core.Authentication auth) {
    boolean isAdmin = auth.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    return isAdmin ? "redirect:/admin" : "redirect:/home";
  }
}
