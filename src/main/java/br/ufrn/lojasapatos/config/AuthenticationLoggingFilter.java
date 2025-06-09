package br.ufrn.lojasapatos.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthenticationLoggingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        logger.info("=== DEBUG SPRING SECURITY ===");
        logger.info("URL: {}", request.getRequestURI());

        if (authentication != null && authentication.isAuthenticated()) {
            logger.info("✅ USUÁRIO LOGADO: '{}'", authentication.getName());
            logger.info("✅ AUTHORITIES: {}", authentication.getAuthorities());
            logger.info("✅ PRINCIPAL: {}", authentication.getPrincipal().getClass().getSimpleName());
        } else {
            logger.info("❌ USUÁRIO NÃO LOGADO");
        }

        logger.info("=== FIM DEBUG ===");

        filterChain.doFilter(request, response);
    }
}
