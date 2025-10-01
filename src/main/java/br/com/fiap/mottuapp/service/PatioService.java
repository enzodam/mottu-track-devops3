package br.com.fiap.mottuapp.service;

import br.com.fiap.mottuapp.model.Moto;
import br.com.fiap.mottuapp.model.Vaga;
import br.com.fiap.mottuapp.repository.MotoRepository;
import br.com.fiap.mottuapp.repository.VagaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PatioService {
  private final VagaRepository vagaRepo;
  private final MotoRepository motoRepo;
  public PatioService(VagaRepository vagaRepo, MotoRepository motoRepo){
    this.vagaRepo = vagaRepo; this.motoRepo = motoRepo;
  }
  public List<Vaga> vagasDisponiveis(){ return vagaRepo.findByDisponivelTrue(); }
  public void alocarMotoEmVaga(Long motoId, Long vagaId){
    Vaga v = vagaRepo.findById(vagaId).orElseThrow();
    Moto m = motoRepo.findById(motoId).orElseThrow();
    v.setDisponivel(false);
    vagaRepo.save(v);
  }
}
