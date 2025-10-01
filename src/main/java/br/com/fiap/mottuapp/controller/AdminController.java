package br.com.fiap.mottuapp.controller;

import br.com.fiap.mottuapp.model.Filial;
import br.com.fiap.mottuapp.model.Moto;
import br.com.fiap.mottuapp.model.Patio;
import br.com.fiap.mottuapp.model.Usuario;
import br.com.fiap.mottuapp.model.Vaga;
import br.com.fiap.mottuapp.repository.FilialRepository;
import br.com.fiap.mottuapp.repository.MotoRepository;
import br.com.fiap.mottuapp.repository.PatioRepository;
import br.com.fiap.mottuapp.repository.UsuarioRepository;
import br.com.fiap.mottuapp.repository.VagaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired private FilialRepository filialRepo;
    @Autowired private PatioRepository patioRepo;
    @Autowired private VagaRepository vagaRepo;
    @Autowired private MotoRepository motoRepo;
    @Autowired private UsuarioRepository usuarioRepo;

    // ========================= DASHBOARD =========================

    @GetMapping
    public String index(Model model,
                        @RequestParam(value = "msg", required = false) String msg,
                        @RequestParam(value = "err", required = false) String err) {

        // Listas para as tabelas
        model.addAttribute("filiaisList", filialRepo.findAll());
        model.addAttribute("patiosList",  patioRepo.findAll());
        model.addAttribute("vagasList",   vagaRepo.findAll());
        model.addAttribute("motosList",   motoRepo.findAll());
        model.addAttribute("usuariosList",usuarioRepo.findAll());

        // Contadores dos cards
        model.addAttribute("filiaisCount",  filialRepo.count());
        model.addAttribute("patiosCount",   patioRepo.count());
        model.addAttribute("vagasCount",    vagaRepo.count());
        model.addAttribute("motosCount",    motoRepo.count());

        if (msg != null) model.addAttribute("msg", msg);
        if (err != null) model.addAttribute("err", err);

        return "home-admin"; // templates/home-admin.html
    }

    /** Alias para compatibilidade: /admin/home aponta para o dashboard */
    @GetMapping("/home")
    public String homeAlias(Model model,
                            @RequestParam(value="msg", required=false) String msg,
                            @RequestParam(value="err", required=false) String err) {
        return index(model, msg, err);
    }


    // ========================= FILIAIS =========================

    @GetMapping("/filiais/buscar")
    public String buscarFilial(@RequestParam("id") Long id, Model model) {
        filialRepo.findById(id).ifPresent(f -> {
            model.addAttribute("filialBusca", f); // para a tabela de resultado
            model.addAttribute("filial", f);      // para o formulário (th:value="${filial?....}")
        });
        return index(model, null, null);
    }


    @PostMapping("/filiais")
    @Transactional
    public String salvarFilial(@RequestParam(value = "id", required = false) Long id,
                               @RequestParam("nome") String nome,
                               @RequestParam("estado") String estado,
                               RedirectAttributes ra) {

        Filial f = (id != null) ? filialRepo.findById(id).orElse(new Filial()) : new Filial();

        f.setNome(nome);
        f.setEstado(estado == null ? null : estado.trim().toUpperCase());

        filialRepo.save(f);

        ra.addAttribute("msg", "Filial salva com sucesso");
        return "redirect:/admin";
    }


    @PostMapping("/filiais/{id}/excluir")
    @Transactional
    public String excluirFilial(@PathVariable("id") Long id, RedirectAttributes ra) {
        if (filialRepo.existsById(id)) {
            filialRepo.deleteById(id);
            ra.addAttribute("msg", "Filial excluída");
        } else {
            ra.addAttribute("err", "Filial não encontrada");
        }
        return "redirect:/admin";
    }

    // ========================= PÁTIOS =========================

    @GetMapping("/patios/buscar")
    public String buscarPatio(@RequestParam("id") Long id, Model model) {
        patioRepo.findById(id).ifPresent(p -> {
            model.addAttribute("patioBusca", p);
            model.addAttribute("patio", p);
        });
        return index(model, null, null);
    }


    @PostMapping("/patios")
    @Transactional
    public String salvarPatio(@RequestParam(value = "id", required = false) Long id,
                              @RequestParam("nome") String nome,
                              @RequestParam(value = "filial.id", required = false) Long filialId,
                              RedirectAttributes ra) {

        Patio p = (id != null) ? patioRepo.findById(id).orElse(new Patio()) : new Patio();
        p.setNome(nome);

        if (filialId == null) {
            ra.addAttribute("err", "Informe a Filial ID");
            return "redirect:/admin";
        }
        Filial filial = filialRepo.findById(filialId).orElse(null);
        if (filial == null) {
            ra.addAttribute("err", "Filial não encontrada");
            return "redirect:/admin";
        }
        p.setFilial(filial);

        patioRepo.save(p);
        ra.addAttribute("msg", "Pátio salvo com sucesso");
        return "redirect:/admin";
    }


    @PostMapping("/patios/{id}/excluir")
    @Transactional
    public String excluirPatio(@PathVariable("id") Long id, RedirectAttributes ra) {
        if (patioRepo.existsById(id)) {
            patioRepo.deleteById(id);
            ra.addAttribute("msg", "Pátio excluído");
        } else {
            ra.addAttribute("err", "Pátio não encontrado");
        }
        return "redirect:/admin";
    }

    // ========================= VAGAS =========================

    @GetMapping("/vagas/buscar")
    public String buscarVaga(@RequestParam("id") Long id, Model model) {
        vagaRepo.findById(id).ifPresent(v -> {
            model.addAttribute("vagaBusca", v);
            model.addAttribute("vaga", v);
        });
        return index(model, null, null);
    }

    @PostMapping("/vagas")
    @Transactional
    public String salvarVaga(@RequestParam(value = "id", required = false) Long id,
                             @RequestParam("codigo") String codigo,
                             @RequestParam(value = "patio.id", required = false) Long patioId,
                             @RequestParam(value = "disponivel", required = false) String disponivel,
                             RedirectAttributes ra) {

        Vaga v = (id != null) ? vagaRepo.findById(id).orElse(new Vaga()) : new Vaga();
        v.setCodigo(codigo);

        if (patioId == null) {
            ra.addAttribute("err", "Informe o Pátio ID");
            return "redirect:/admin";
        }
        Patio patio = patioRepo.findById(patioId).orElse(null);
        if (patio == null) {
            ra.addAttribute("err", "Pátio não encontrado");
            return "redirect:/admin";
        }
        v.setPatio(patio);

        v.setDisponivel(disponivel != null);

        vagaRepo.save(v);
        ra.addAttribute("msg", "Vaga salva com sucesso");
        return "redirect:/admin";
    }


    @PostMapping("/vagas/{id}/excluir")
    @Transactional
    public String excluirVaga(@PathVariable("id") Long id, RedirectAttributes ra) {
        if (vagaRepo.existsById(id)) {
            vagaRepo.deleteById(id);
            ra.addAttribute("msg", "Vaga excluída");
        } else {
            ra.addAttribute("err", "Vaga não encontrada");
        }
        return "redirect:/admin";
    }

    // ========================= MOTOS =========================

    @GetMapping("/motos/buscar")
    public String buscarMoto(@RequestParam("id") Long id, Model model) {
        motoRepo.findById(id).ifPresent(m -> {
            model.addAttribute("motoBusca", m);
            model.addAttribute("moto", m);
        });
        return index(model, null, null);
    }


    @PostMapping("/motos")
    @Transactional
    public String salvarMoto(@RequestParam(value = "id", required = false) Long id,
                             @RequestParam("placa") String placa,
                             @RequestParam("cor") String cor,
                             @RequestParam(value = "filial.id", required = false) Long filialId,
                             RedirectAttributes ra) {

        Moto m = (id != null) ? motoRepo.findById(id).orElse(new Moto()) : new Moto();
        m.setPlaca(placa);
        m.setCor(cor);

        if (filialId == null) {
            ra.addAttribute("err", "Informe a Filial ID");
            return "redirect:/admin";
        }
        Filial filial = filialRepo.findById(filialId).orElse(null);
        if (filial == null) {
            ra.addAttribute("err", "Filial não encontrada");
            return "redirect:/admin";
        }
        m.setFilial(filial);

        motoRepo.save(m);
        ra.addAttribute("msg", "Moto salva com sucesso");
        return "redirect:/admin";
    }


    @PostMapping("/motos/{id}/excluir")
    @Transactional
    public String excluirMoto(@PathVariable("id") Long id, RedirectAttributes ra) {
        if (motoRepo.existsById(id)) {
            motoRepo.deleteById(id);
            ra.addAttribute("msg", "Moto excluída");
        } else {
            ra.addAttribute("err", "Moto não encontrada");
        }
        return "redirect:/admin";
    }

    // ========================= USUÁRIOS =========================

    @GetMapping("/usuarios/buscar")
    public String buscarUsuario(@RequestParam("id") Long id, Model model) {
        usuarioRepo.findById(id).ifPresent(u -> {
            model.addAttribute("usuarioBusca", u);
            model.addAttribute("usuario", u);
        });
        return index(model, null, null);
    }


    @PostMapping("/usuarios")
    @Transactional
    public String salvarUsuario(@RequestParam(value = "id", required = false) Long id,
                                @RequestParam("nome") String nome,
                                @RequestParam("email") String email,
                                @RequestParam(value = "senha", required = false) String senha,
                                @RequestParam(value = "ativo", required = false) String ativo,
                                @RequestParam(value = "perfil", required = false) String perfil,
                                RedirectAttributes ra,
                                Model model) {

        Usuario u;

        if (id != null) {
            // UPDATE
            Optional<Usuario> opt = usuarioRepo.findById(id);
            if (opt.isEmpty()) {
                ra.addAttribute("err", "Usuário não encontrado");
                return "redirect:/admin";
            }
            u = opt.get();

            u.setNome(nome);
            u.setEmail(email);
            u.setAtivo(ativo != null); // checkbox

            // se senha veio preenchida, atualiza; se veio vazia, mantém a antiga
            if (StringUtils.hasText(senha)) {
                u.setSenha(senha); // se usar PasswordEncoder aqui, troque por passwordEncoder.encode(senha)
            }

            // garante obrigatórios
            if (!StringUtils.hasText(u.getUsername())) {
                u.setUsername(email);
            }
            if (StringUtils.hasText(perfil)) {
                u.setPerfil(perfil);
            } else if (!StringUtils.hasText(u.getPerfil())) {
                u.setPerfil("USER");
            }

            usuarioRepo.save(u);
            ra.addAttribute("msg", "Usuário atualizado");
            return "redirect:/admin";

        } else {
            // CREATE (exige senha para não gerar usuário inválido)
            if (!StringUtils.hasText(senha)) {
                // volta para a tela com erro amigável
                model.addAttribute("err", "Para cadastrar novo usuário, informe a senha.");
                return index(model, null, null);
            }

            u = new Usuario();
            u.setNome(nome);
            u.setEmail(email);
            u.setSenha(senha); // se usar PasswordEncoder aqui, troque por passwordEncoder.encode(senha)
            u.setAtivo(ativo != null);

            // obrigatórios
            u.setUsername(email);
            u.setPerfil(StringUtils.hasText(perfil) ? perfil : "USER");

            usuarioRepo.save(u);
            ra.addAttribute("msg", "Usuário criado");
            return "redirect:/admin";
        }
    }


    @PostMapping("/usuarios/{id}/excluir")
    @Transactional
    public String excluirUsuario(@PathVariable("id") Long id, RedirectAttributes ra) {
        if (usuarioRepo.existsById(id)) {
            usuarioRepo.deleteById(id);
            ra.addAttribute("msg", "Usuário excluído");
        } else {
            ra.addAttribute("err", "Usuário não encontrado");
        }
        return "redirect:/admin";
    }
}
