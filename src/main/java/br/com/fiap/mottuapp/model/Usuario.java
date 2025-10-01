package br.com.fiap.mottuapp.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable=false, unique = true, length = 80)
  private String username;

  @Column(nullable=false, length = 150)
  private String senha;

  @Column(nullable=false, length = 120)
  private String nome;

  @Column(nullable=false, unique = true, length = 120)
  private String email;

  @Column(nullable=false, length = 20)
  private String perfil; // "ADMIN" ou "USER"

  @Builder.Default
  @Column(nullable=false)
  private Boolean ativo = true;

  public boolean isAtivo() {
    return Boolean.TRUE.equals(ativo);
  }
}
