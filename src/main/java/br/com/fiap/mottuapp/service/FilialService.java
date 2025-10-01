package br.com.fiap.mottuapp.service;

import br.com.fiap.mottuapp.model.Filial;
import br.com.fiap.mottuapp.repository.FilialRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FilialService {
  private final FilialRepository repo;
  public FilialService(FilialRepository repo) { this.repo = repo; }
  public List<Filial> listar() { return repo.findAll(); }
  public Filial buscar(Long id){ return repo.findById(id).orElseThrow(); }
  public Filial salvar(Filial f){ return repo.save(f); }
  public void excluir(Long id){ repo.deleteById(id); }
}
