package br.com.fiap.mottuapp.repository;

import br.com.fiap.mottuapp.model.Moto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MotoRepository extends JpaRepository<Moto, Long> {
    List<Moto> findByFilialId(Long filialId);
}
