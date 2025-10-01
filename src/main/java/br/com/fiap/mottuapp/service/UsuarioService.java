package br.com.fiap.mottuapp.service;

import br.com.fiap.mottuapp.model.Usuario;
import br.com.fiap.mottuapp.repository.UsuarioRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

  private final UsuarioRepository usuarioRepository;
  private final PasswordEncoder passwordEncoder;

  /* ===================== REGRAS/CRUD ===================== */

  @Transactional
  public Usuario registrar(@Valid Usuario dto) {
    validarPerfil(dto.getPerfil());
    usuarioRepository.findByUsername(dto.getUsername()).ifPresent(u -> {
      throw new IllegalArgumentException("Username já está em uso");
    });
    if (usuarioRepository.existsByEmail(dto.getEmail())) {
      throw new IllegalArgumentException("E-mail já está em uso");
    }

    dto.setSenha(passwordEncoder.encode(dto.getSenha()));
    if (dto.getPerfil() == null || dto.getPerfil().isBlank()) dto.setPerfil("USER");
    if (dto.getAtivo() == null) dto.setAtivo(true);

    return usuarioRepository.save(dto);
  }

  @Transactional
  public Usuario criar(@Valid Usuario dto) { return registrar(dto); }

  @Transactional
  public Usuario atualizar(Long id, @Valid Usuario dados) {
    validarPerfil(dados.getPerfil());
    Usuario u = buscarPorId(id);

    if (dados.getUsername() != null && !dados.getUsername().isBlank()) {
      usuarioRepository.findByUsername(dados.getUsername())
              .filter(existente -> !existente.getId().equals(id))
              .ifPresent(existente -> { throw new IllegalArgumentException("Username já está em uso"); });
      u.setUsername(dados.getUsername());
    }
    if (dados.getNome() != null) u.setNome(dados.getNome());
    if (dados.getEmail() != null) u.setEmail(dados.getEmail());

    if (dados.getSenha() != null && !dados.getSenha().isBlank()) {
      u.setSenha(passwordEncoder.encode(dados.getSenha()));
    }
    if (dados.getPerfil() != null && !dados.getPerfil().isBlank()) {
      u.setPerfil(normalizarPerfil(dados.getPerfil()));
    }
    if (dados.getAtivo() != null) {
      u.setAtivo(dados.getAtivo());
    }

    return usuarioRepository.save(u);
  }

  @Transactional public void excluir(Long id) { usuarioRepository.deleteById(id); }

  @Transactional(readOnly = true)
  public Usuario buscarPorId(Long id) {
    return usuarioRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário id=" + id + " não encontrado"));
  }

  @Transactional(readOnly = true) public List<Usuario> listar() { return usuarioRepository.findAll(); }

  @Transactional
  public void alterarSenha(Long id, String novaSenha) {
    if (novaSenha == null || novaSenha.isBlank()) {
      throw new IllegalArgumentException("Senha não pode ser vazia");
    }
    Usuario u = buscarPorId(id);
    u.setSenha(passwordEncoder.encode(novaSenha));
    usuarioRepository.save(u);
  }

  @Transactional
  public void alterarPerfil(Long id, String novoPerfil) {
    validarPerfil(novoPerfil);
    Usuario u = buscarPorId(id);
    u.setPerfil(normalizarPerfil(novoPerfil));
    usuarioRepository.save(u);
  }

  @Transactional public void ativar(Long id)   { Usuario u = buscarPorId(id); u.setAtivo(true);  usuarioRepository.save(u); }
  @Transactional public void desativar(Long id) { Usuario u = buscarPorId(id); u.setAtivo(false); usuarioRepository.save(u); }

  /* ============== INTEGRAÇÃO SPRING SECURITY ============== */

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    Usuario u = usuarioRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

    // mais seguro para Boolean (evita NPE e independe de get/is)
    if (!Boolean.TRUE.equals(u.getAtivo())) {
      throw new UsernameNotFoundException("Usuário inativo");
    }

    // roles(...) adiciona automaticamente o prefixo ROLE_
    return User.withUsername(u.getUsername())
            .password(u.getSenha()) // já em BCrypt
            .roles(normalizarPerfil(u.getPerfil())) // aceita ADMIN/USER ou ROLE_ADMIN/ROLE_USER
            .build();
  }

  /* ======================= HELPERS ======================= */

  private void validarPerfil(String perfil) {
    if (perfil == null || perfil.isBlank()) return; // default USER no registrar()
    String p = normalizarPerfil(perfil);
    if (!p.equals("ADMIN") && !p.equals("USER")) {
      throw new IllegalArgumentException("Perfil inválido. Use ADMIN ou USER.");
    }
  }

  /** Normaliza para maiúsculas sem prefixo ROLE_ */
  private String normalizarPerfil(String perfil) {
    if (perfil == null) return "USER";
    return perfil.trim().toUpperCase().replaceFirst("^ROLE_", "");
  }
}
