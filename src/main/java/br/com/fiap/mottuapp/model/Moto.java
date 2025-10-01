package br.com.fiap.mottuapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Moto {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank @Size(max=10)
  @Column(nullable=false, unique=true, length=10)
  private String placa;

  @NotBlank @Size(max=20)
  @Column(nullable=false, length=20)
  private String cor;

  @ManyToOne(optional=false)
  private Filial filial;

  @Builder.Default
  @Column(nullable=false)
  private Boolean disponivel = true;
}
