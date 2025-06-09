package br.ufrn.lojasapatos.controller;

import br.ufrn.lojasapatos.model.Usuario;
import br.ufrn.lojasapatos.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Usuário ou senha inválidos!");
        }
        return "login";
    }

    @GetMapping("/cadUsuario")
    public String cadUsuario(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "cadUsuario";
    }

    @PostMapping("/salvarUsuario")
    public String salvarUsuario(@Valid @ModelAttribute Usuario usuario, BindingResult result,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "cadUsuario";
        }

        // Verificar se username já existe
        if (usuarioService.findByUsername(usuario.getUsername()) != null) {
            result.rejectValue("username", "error.usuario", "Username já existe!");
            return "cadUsuario";
        }

        usuarioService.save(usuario);
        redirectAttributes.addFlashAttribute("mensagem", "Usuário cadastrado com sucesso!");
        return "redirect:/login";
    }
}
