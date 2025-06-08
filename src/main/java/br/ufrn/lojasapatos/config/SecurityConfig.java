package br.ufrn.lojasapatos.config;

import br.ufrn.lojasapatos.service.UsuarioService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // REMOVER @Autowired e configureGlobal que causam dependência circular

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
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
                        .defaultSuccessUrl("/", true)
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
