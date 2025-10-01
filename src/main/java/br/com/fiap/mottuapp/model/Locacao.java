package br.com.fiap.mottuapp.model;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Locacao {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Usuario usuario;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Moto moto;

    private LocalDateTime inicio;

    private LocalDateTime fimPrevisto;

    private LocalDateTime fimEfetivo;

    @Column(nullable = false, length = 20)
    private String status; // "ATIVA" | "ENCERRADA" | "CANCELADA"

    @Column(precision = 12, scale = 2)
    private BigDecimal valorTotal;
}
