package br.com.fiap.mottuapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Vaga {
  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank @Size(max=20)
  @Column(nullable=false, unique=true, length=20)
  private String codigo;

  @Builder.Default
  @Column(nullable=false)
  private Boolean disponivel = true;

  @ManyToOne(optional=false)
  private Patio patio;

  public boolean isDisponivel() {
    return Boolean.TRUE.equals(disponivel);
  }

}
