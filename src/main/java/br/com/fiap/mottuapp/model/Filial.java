package br.com.fiap.mottuapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Filial {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank @Size(max=120)
  @Column(nullable=false, length=120)
  private String nome;

  @NotBlank @Size(max=2)
  @Column(nullable=false, length=2)
  private String estado;
}
