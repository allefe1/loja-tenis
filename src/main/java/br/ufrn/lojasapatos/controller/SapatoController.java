package br.ufrn.lojasapatos.controller;

import br.ufrn.lojasapatos.model.Sapato;
import br.ufrn.lojasapatos.service.SapatoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.security.core.Authentication;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

@Controller
public class SapatoController {

    private static final Logger logger = LoggerFactory.getLogger(SapatoController.class);
    private final SapatoService sapatoService;

    public SapatoController(SapatoService sapatoService) {
        this.sapatoService = sapatoService;
    }

    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields("isDeleted");
    }

    
    @SuppressWarnings("unchecked")
    private List<Sapato> getCarrinhoFromSession(HttpSession session) {
        Object carrinhoObj = session.getAttribute("carrinho");
        if (carrinhoObj instanceof List<?>) {
            return (List<Sapato>) carrinhoObj;
        }
        return new ArrayList<>();
    }

    @GetMapping("/")
    public String index(Model model, @RequestParam(value = "mensagem", required = false) String mensagem) {
        List<Sapato> sapatosList = sapatoService.findAllNotDeleted();
        model.addAttribute("sapatosList", sapatosList);
        model.addAttribute("currentPage", "home");

        if (mensagem != null) {
            model.addAttribute("mensagem", mensagem);
        }

        return "index";
    }

    @GetMapping("/admin")
    public String admin(Model model, @RequestParam(value = "mensagem", required = false) String mensagem) {
        logger.info("Acessando página de administração");
        List<Sapato> allSapatos = sapatoService.findAll();
        model.addAttribute("sapatosList", allSapatos);
        model.addAttribute("currentPage", "admin");

        if (mensagem != null) {
            model.addAttribute("mensagem", mensagem);
            logger.debug("Mensagem adicionada ao model: {}", mensagem);
        }

        return "admin";
    }

    @GetMapping("/cadastro")
    public String cadastro(Model model) {
        logger.info("Acessando página de cadastro de sapato");
        Sapato sapato = new Sapato();
        model.addAttribute("sapato", sapato);
        model.addAttribute("currentPage", "cadastro");
        logger.debug("Sapato criado e adicionado ao model");
        return "cadastro";
    }

    @GetMapping("/editar")
    public String editar(@RequestParam Long id, Model model) {
        logger.info("Editando sapato com ID: {}", id);
        Sapato sapato = sapatoService.findById(id);
        if (sapato == null) {
            logger.warn("Sapato não encontrado com ID: {}", id);
            return "redirect:/admin";
        }
        model.addAttribute("sapato", sapato);
        model.addAttribute("currentPage", "editar");
        logger.debug("Sapato encontrado para edição: {}", sapato.getNome());
        return "cadastro";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid @ModelAttribute Sapato sapato, BindingResult result,
                         RedirectAttributes redirectAttributes) {

        logger.info("Tentativa de salvar sapato: {}", sapato.getNome());
        logger.debug("Dados do sapato - ID: {}, Nome: {}, Marca: {}, Preço: {}",
                sapato.getId(), sapato.getNome(), sapato.getMarca(), sapato.getPreco());

        // GARANTIR que isDeleted seja null para novos sapatos
        if (sapato.getId() == null) {
            sapato.setIsDeleted(null);
            logger.debug("Novo sapato - isDeleted definido como null");
        }

        if (result.hasErrors()) {
            logger.warn("Erros de validação encontrados ao salvar sapato");
            result.getAllErrors().forEach(this::logValidationError);
            return "cadastro";
        }

        try {
            // Selecionar imagem aleatoriamente conforme Questão 7 do PDF
            if (sapato.getImageUrl() == null || sapato.getImageUrl().isEmpty()) {
                String[] imagens = {"/images/sapato1.png", "/images/sapato2.png",
                        "/images/sapato3.png", "/images/sapato4.png"};
                Random random = new Random();
                String imagemSelecionada = imagens[random.nextInt(imagens.length)];
                sapato.setImageUrl(imagemSelecionada);
                logger.debug("Imagem selecionada aleatoriamente: {}", imagemSelecionada);
            }

            Sapato sapatoSalvo = sapatoService.save(sapato);
            logger.info("Sapato salvo com sucesso! ID: {}", sapatoSalvo.getId());

            String mensagem = sapato.getId() != null ?
                    "Sapato atualizado com sucesso!" : "Sapato cadastrado com sucesso!";
            redirectAttributes.addFlashAttribute("mensagem", mensagem);

            return "redirect:/admin";

        } catch (Exception e) {
            logger.error("Erro ao salvar sapato: {}", sapato.getNome(), e);
            redirectAttributes.addFlashAttribute("erro", "Erro ao salvar sapato: " + e.getMessage());
            redirectAttributes.addFlashAttribute("sapato", sapato);
            return "redirect:/cadastro";
        }
    }

    
    private void logValidationError(org.springframework.validation.ObjectError error) {
        logger.error("Erro de validação: {} - Campo: {}",
                error.getDefaultMessage(),
                error instanceof org.springframework.validation.FieldError ?
                        ((org.springframework.validation.FieldError) error).getField() : "global");
    }

    @GetMapping("/deletar")
    public String deletar(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        logger.info("Tentativa de deletar sapato com ID: {}", id);
        try {
            sapatoService.softDelete(id);
            logger.info("Sapato deletado com sucesso - ID: {}", id);
            redirectAttributes.addFlashAttribute("mensagem", "Sapato removido com sucesso!");
        } catch (Exception e) {
            logger.error("Erro ao deletar sapato com ID: {}", id, e);
            redirectAttributes.addFlashAttribute("erro", "Erro ao remover sapato");
        }
        return "redirect:/admin";
    }

    @GetMapping("/restaurar")
    public String restaurar(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        logger.info("Tentativa de restaurar sapato com ID: {}", id);
        try {
            sapatoService.restore(id);
            logger.info("Sapato restaurado com sucesso - ID: {}", id);
            redirectAttributes.addFlashAttribute("mensagem", "Sapato restaurado com sucesso!");
        } catch (Exception e) {
            logger.error("Erro ao restaurar sapato com ID: {}", id, e);
            redirectAttributes.addFlashAttribute("erro", "Erro ao restaurar sapato");
        }
        return "redirect:/admin";
    }

    // Questão 9 - adicionarCarrinho 
    @GetMapping("/adicionarCarrinho")
    public String adicionarCarrinho(@RequestParam Long id, HttpSession session,
                                    Authentication authentication, RedirectAttributes redirectAttributes) {

        // Verificação adicional conforme Questão 12
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("erro", "Você precisa estar logado para adicionar itens ao carrinho.");
            return "redirect:/login";
        }

        logger.info("Adicionando sapato ao carrinho - ID: {} - Usuário: {}", id, authentication.getName());
        Sapato sapato = sapatoService.findById(id);

        if (sapato != null && sapato.getIsDeleted() == null) {
            List<Sapato> carrinho = getCarrinhoFromSession(session);
            carrinho.add(sapato);
            session.setAttribute("carrinho", carrinho);
            logger.debug("Sapato adicionado ao carrinho. Total de itens: {}", carrinho.size());
        } else {
            logger.warn("Sapato não encontrado ou deletado - ID: {}", id);
        }

        return "redirect:/";
    }

    // Questão 10 - verCarrinho
    @GetMapping("/verCarrinho")
    public String verCarrinho(HttpSession session, Model model, Authentication authentication,
                              RedirectAttributes redirectAttributes) {

        // Verificação adicional conforme Questão 12
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("erro", "Você precisa estar logado para ver o carrinho.");
            return "redirect:/login";
        }

        logger.info("Visualizando carrinho - Usuário: {}", authentication.getName());
        List<Sapato> carrinho = getCarrinhoFromSession(session);
        logger.debug("Itens no carrinho: {}", carrinho.size());

        if (carrinho.isEmpty()) {
            redirectAttributes.addFlashAttribute("mensagem", "Não existem itens no carrinho");
            return "redirect:/";
        }

        double total = carrinho.stream()
                .mapToDouble(sapato -> sapato.getPreco().doubleValue())
                .sum();

        logger.debug("Total do carrinho: R$ {}", total);
        model.addAttribute("carrinho", carrinho);
        model.addAttribute("total", total);
        model.addAttribute("currentPage", "carrinho");
        return "carrinho";
    }

    // Questão 11 - finalizarCompra 
    @GetMapping("/finalizarCompra")
    public String finalizarCompra(HttpSession session, Authentication authentication,
                                  RedirectAttributes redirectAttributes) {

        
        if (authentication == null || !authentication.isAuthenticated()) {
            redirectAttributes.addFlashAttribute("erro", "Você precisa estar logado para finalizar a compra.");
            return "redirect:/login";
        }

        logger.info("Finalizando compra - Usuário: {}", authentication.getName());

        
        session.removeAttribute("carrinho");
        logger.info("Carrinho limpo com sucesso - compra finalizada para usuário: {}", authentication.getName());

        redirectAttributes.addFlashAttribute("mensagem", "Compra finalizada com sucesso! Obrigado pela preferência.");
        return "redirect:/";
    }
}
