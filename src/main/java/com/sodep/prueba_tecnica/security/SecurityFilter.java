package com.sodep.prueba_tecnica.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.List;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    @Value("${internal.api.token}")
    private String internalApiToken;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        if (path.equals("/api/clientes/sync")) {
            filterChain.doFilter(request, response);
            return;
        }
        // Obtener el token de la cabecera Authorization
        String authHeader = request.getHeader("Authorization");

        // Debug logging for troubleshooting
        System.out.println("authHeader: [" + authHeader + "]");
        System.out.println("expected:  [Bearer " + internalApiToken + "]");
        // Validar el token
        if (authHeader == null || !authHeader.equals("Bearer " + internalApiToken)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Invalid or missing token");
            return;
        }
        // Agregamos autenticacion al contexto de seguridad (hardcoded ya que no hay
        // usuarios reales)
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("apiUser", null,
                List.of(new SimpleGrantedAuthority("ROLE_USER")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
