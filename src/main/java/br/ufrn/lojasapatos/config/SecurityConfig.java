package br.ufrn.lojasapatos.config;

import br.ufrn.lojasapatos.service.UsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
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

                // Redirecionamento baseado em roles conforme Questão 12 do PDF
                if (roles.contains("ROLE_ADMIN")) {
                    response.sendRedirect("/admin");  // Admin vai para painel administrativo
                } else if (roles.contains("ROLE_USER")) {
                    response.sendRedirect("/");       // User comum vai para home
                } else {
                    response.sendRedirect("/");       // Fallback para home
                }
            }
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // Páginas públicas conforme especificado no PDF
                        .requestMatchers("/", "/login", "/cadUsuario", "/salvarUsuario").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()

                        // Apenas ROLE_ADMIN conforme Questão 12
                        .requestMatchers("/admin", "/cadastro", "/salvar", "/editar", "/deletar", "/restaurar").hasRole("ADMIN")

                        // Apenas ROLE_USER conforme Questão 12
                        .requestMatchers("/verCarrinho", "/adicionarCarrinho", "/finalizarCompra").hasRole("USER")

                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .successHandler(customAuthenticationSuccessHandler())  // ← MUDANÇA PRINCIPAL
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }
}
