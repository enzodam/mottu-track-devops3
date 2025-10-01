package br.com.fiap.mottuapp.service;

import br.com.fiap.mottuapp.model.Patio;
import br.com.fiap.mottuapp.model.Vaga;
import br.com.fiap.mottuapp.repository.PatioRepository;
import br.com.fiap.mottuapp.repository.VagaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VagaService {
  private final VagaRepository repo;
  private final PatioRepository patioRepo;
  public VagaService(VagaRepository repo, PatioRepository patioRepo){
    this.repo = repo; this.patioRepo = patioRepo;
  }
  public List<Vaga> listar(){ return repo.findAll(); }
  public Vaga buscar(Long id){ return repo.findById(id).orElseThrow(); }
  public Vaga salvar(Vaga v, Long patioId){
    Patio p = patioRepo.findById(patioId).orElseThrow();
    v.setPatio(p);
    if (v.getCodigo()==null || v.getCodigo().isBlank()) throw new IllegalArgumentException("Código obrigatório");
    return repo.save(v);
  }
  public void excluir(Long id){ repo.deleteById(id); }
  public List<Vaga> vagasDisponiveis(){ return repo.findByDisponivelTrue(); }
}
