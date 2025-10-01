package br.com.fiap.mottuapp.controller;

import br.com.fiap.mottuapp.model.Filial;
import br.com.fiap.mottuapp.model.Moto;
import br.com.fiap.mottuapp.model.Vaga;
import br.com.fiap.mottuapp.repository.FilialRepository;
import br.com.fiap.mottuapp.repository.MotoRepository;
import br.com.fiap.mottuapp.repository.VagaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/app/aluguel")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class AppController {

    private final FilialRepository filialRepo;
    private final MotoRepository motoRepo;
    private final VagaRepository vagaRepo;

    @GetMapping
    public String escolher(Model model) {
        model.addAttribute("filiais", filialRepo.findAll());
        model.addAttribute("motos", null);
        model.addAttribute("vagas", vagaRepo.findByDisponivelTrue());
        return "app/aluguel/escolher";
    }

    @GetMapping("/filial/{filialId}")
    public String porFilial(@PathVariable Long filialId, Model model) {
        Filial f = filialRepo.findById(filialId).orElseThrow();
        model.addAttribute("filiais", filialRepo.findAll());
        model.addAttribute("filialSelecionada", f);
        model.addAttribute("motos", motoRepo.findByFilialId(filialId));
        model.addAttribute("vagas", vagaRepo.findByDisponivelTrue());
        return "app/aluguel/escolher";
    }

    @PostMapping("/confirmar")
    public String confirmar(@RequestParam Long filialId,
                            @RequestParam Long motoId,
                            @RequestParam Long vagaId,
                            Model model) {
        Moto moto = motoRepo.findById(motoId).orElseThrow();
        Vaga vaga = vagaRepo.findById(vagaId).orElseThrow();
        if (!vaga.isDisponivel()) return "redirect:/app/aluguel?vagaIndisponivel";
        vaga.setDisponivel(false);
        vagaRepo.save(vaga);
        model.addAttribute("moto", moto);
        model.addAttribute("vaga", vaga);
        model.addAttribute("filial", filialRepo.findById(filialId).orElseThrow());
        return "app/aluguel/ok";
    }
}
