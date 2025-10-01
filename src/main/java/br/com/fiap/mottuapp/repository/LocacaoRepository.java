package br.com.fiap.mottuapp.repository;

import br.com.fiap.mottuapp.model.Locacao;
import br.com.fiap.mottuapp.model.Moto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LocacaoRepository extends JpaRepository<Locacao, Long> {
    boolean existsByMotoAndStatus(Moto moto, String status);
    Optional<Locacao> findFirstByMotoIdAndStatus(Long motoId, String status);
}
