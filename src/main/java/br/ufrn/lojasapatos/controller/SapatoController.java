package br.ufrn.lojasapatos.controller;

import br.ufrn.lojasapatos.model.Sapato;
import br.ufrn.lojasapatos.service.SapatoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import java.util.Random;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Random;


import java.util.List;

@Controller
public class SapatoController {

    @Autowired
    private SapatoService sapatoService;

    @GetMapping("/")
    public String index(Model model) {
        List<Sapato> sapatosList = sapatoService.findAllNotDeleted();
        model.addAttribute("sapatosList", sapatosList);
        return "index";
    }

    @GetMapping("/admin")
    public String admin(Model model) {
        List<Sapato> allSapatos = sapatoService.findAll();
        model.addAttribute("sapatosList", allSapatos);
        return "admin";
    }

    @GetMapping("/cadastro")
    public String cadastro(Model model) {
        model.addAttribute("sapato", new Sapato());
        return "cadastro";
    }

    @GetMapping("/editar")
    public String editar(@RequestParam Long id, Model model) {
        Sapato sapato = sapatoService.findById(id);
        if (sapato == null) {
            return "redirect:/admin";
        }
        model.addAttribute("sapato", sapato);
        return "cadastro"; // Reutiliza o mesmo template
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Sapato sapato, BindingResult result,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "cadastro";
        }

        // Selecionar imagem aleatoriamente conforme especificado no PDF
        if (sapato.getImageUrl() == null || sapato.getImageUrl().isEmpty()) {
            String[] imagens = {"/images/sapato1.jpg", "/images/sapato2.jpg",
                    "/images/sapato3.jpg", "/images/sapato4.jpg"};
            Random random = new Random();
            sapato.setImageUrl(imagens[random.nextInt(imagens.length)]);
        }

        sapatoService.save(sapato);

        String mensagem = sapato.getId() != null ?
                "Sapato atualizado com sucesso!" : "Sapato cadastrado com sucesso!";
        redirectAttributes.addFlashAttribute("mensagem", mensagem);

        return "redirect:/admin";
    }

    @GetMapping("/deletar")
    public String deletar(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        sapatoService.softDelete(id);
        redirectAttributes.addFlashAttribute("mensagem", "Sapato removido com sucesso!");
        return "redirect:/admin";
    }

    @GetMapping("/restaurar")
    public String restaurar(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        sapatoService.restore(id);
        redirectAttributes.addFlashAttribute("mensagem", "Sapato restaurado com sucesso!");
        return "redirect:/admin";
    }

    @GetMapping("/adicionarCarrinho")
    public String adicionarCarrinho(@RequestParam Long id, HttpSession session) {
        Sapato sapato = sapatoService.findById(id);

        if (sapato != null && sapato.getIsDeleted() == null) {
            // Recuperar carrinho da sessão ou criar novo
            List<Sapato> carrinho = (List<Sapato>) session.getAttribute("carrinho");
            if (carrinho == null) {
                carrinho = new ArrayList<>();
            }

            // Adicionar sapato ao carrinho
            carrinho.add(sapato);
            session.setAttribute("carrinho", carrinho);
        }

        return "redirect:/";
    }

    @GetMapping("/verCarrinho")
    public String verCarrinho(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        List<Sapato> carrinho = (List<Sapato>) session.getAttribute("carrinho");

        if (carrinho == null || carrinho.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagem", "Não existem itens no carrinho");
            return "redirect:/";
        }

        // Calcular total do carrinho
        double total = carrinho.stream()
                .mapToDouble(sapato -> sapato.getPreco().doubleValue())
                .sum();

        model.addAttribute("carrinho", carrinho);
        model.addAttribute("total", total);
        return "carrinho";
    }

    @GetMapping("/finalizarCompra")
    public String finalizarCompra(HttpSession session, RedirectAttributes redirectAttributes) {
        // Invalidar a sessão conforme especificado no PDF
        session.invalidate();

        redirectAttributes.addFlashAttribute("mensagem", "Compra finalizada com sucesso! Obrigado pela preferência.");
        return "redirect:/";
    }


}
