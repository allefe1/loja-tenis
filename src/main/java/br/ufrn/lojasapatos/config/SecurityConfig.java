package br.ufrn.lojasapatos.config;

import br.ufrn.lojasapatos.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler customAuthenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Authentication authentication)
                    throws IOException, ServletException {

                Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());

                // Logs de debug detalhados
                logger.info("LOGIN REALIZADO COM SUCESSO!");
                logger.info("Usuario: {}", authentication.getName());
                logger.info("Roles: {}", roles);
                logger.info("Principal: {}", authentication.getPrincipal().getClass().getSimpleName());
                logger.info("Authenticated: {}", authentication.isAuthenticated());

                // Redirecionamento baseado em roles conforme Questão 12 do PDF
                if (roles.contains("ROLE_ADMIN")) {
                    logger.info("Redirecionando ADMIN para /admin");
                    response.sendRedirect("/admin");
                } else if (roles.contains("ROLE_USER")) {
                    logger.info("Redirecionando USER para /");
                    response.sendRedirect("/");
                } else {
                    logger.info("Redirecionando para / (fallback) - Roles: {}", roles);
                    response.sendRedirect("/");
                }
            }
        };
    }

    @Bean
    public AuthenticationFailureHandler customAuthenticationFailureHandler() {
        return (request, response, exception) -> {
            logger.error("FALHA NO LOGIN: {}", exception.getMessage());
            logger.error("Username tentado: {}", request.getParameter("username"));
            response.sendRedirect("/login?error=true");
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        logger.info("Configurando Spring Security...");

        http
                .authorizeHttpRequests(authz -> {
                    logger.info("Configurando autorizacao de requisicoes...");
                    authz
                            // Páginas públicas conforme especificado no PDF
                            .requestMatchers("/", "/login", "/cadUsuario", "/salvarUsuario").permitAll()
                            .requestMatchers("/css/**", "/js/**", "/images/**", "/fonts/**").permitAll()

                            // Apenas ROLE_ADMIN conforme Questão 12
                            .requestMatchers("/admin", "/cadastro", "/salvar", "/editar", "/deletar", "/restaurar").hasRole("ADMIN")

                            // CORRIGIDO: Apenas usuários autenticados conforme Questão 12
                            .requestMatchers("/verCarrinho", "/adicionarCarrinho", "/finalizarCompra").authenticated()

                            // Qualquer usuário autenticado pode acessar outras páginas
                            .anyRequest().authenticated();
                })
                .formLogin(form -> {
                    logger.info("Configurando form login...");
                    form
                            .loginPage("/login")
                            .successHandler(customAuthenticationSuccessHandler())
                            .failureHandler(customAuthenticationFailureHandler())
                            .permitAll();
                })
                .logout(logout -> {
                    logger.info("Configurando logout...");
                    logout
                            .logoutUrl("/logout")
                            .logoutSuccessUrl("/")
                            .invalidateHttpSession(true)
                            .deleteCookies("JSESSIONID")
                            .permitAll();
                })
                .sessionManagement(session -> {
                    logger.info("Configurando gerenciamento de sessao...");
                    session
                            .maximumSessions(1)
                            .maxSessionsPreventsLogin(false);
                });

        logger.info("Spring Security configurado com sucesso!");
        return http.build();
    }
}
