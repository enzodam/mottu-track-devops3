package br.com.fiap.mottuapp.service;

import br.com.fiap.mottuapp.model.Filial;
import br.com.fiap.mottuapp.model.Moto;
import br.com.fiap.mottuapp.repository.FilialRepository;
import br.com.fiap.mottuapp.repository.MotoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MotoService {
  private final MotoRepository repo;
  private final FilialRepository filialRepo;
  public MotoService(MotoRepository repo, FilialRepository filialRepo){
    this.repo = repo; this.filialRepo = filialRepo;
  }
  public List<Moto> listar(){ return repo.findAll(); }
  public Moto buscar(Long id){ return repo.findById(id).orElseThrow(); }
  public Moto salvar(Moto m, Long filialId){
    Filial f = filialRepo.findById(filialId).orElseThrow();
    m.setFilial(f);
    return repo.save(m);
  }
  public void excluir(Long id){ repo.deleteById(id); }
  public void transferir(Long motoId, Long filialDestinoId){
    Moto m = buscar(motoId);
    Filial f = filialRepo.findById(filialDestinoId).orElseThrow();
    m.setFilial(f);
    repo.save(m);
  }
}
