package br.com.fiap.mottuapp.repository;

import br.com.fiap.mottuapp.model.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VagaRepository extends JpaRepository<Vaga, Long> {
  List<Vaga> findByDisponivelTrue();
}
