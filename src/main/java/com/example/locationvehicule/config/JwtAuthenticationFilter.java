package com.example.locationvehicule.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = getJwtFromRequest(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            if (authentication != null) {
                // Vérifier que l'authentification est une instance de UsernamePasswordAuthenticationToken
                if (authentication instanceof org.springframework.security.authentication.UsernamePasswordAuthenticationToken) {
                    // Cast l'authentification en UsernamePasswordAuthenticationToken
                    org.springframework.security.authentication.UsernamePasswordAuthenticationToken authToken =
                            (org.springframework.security.authentication.UsernamePasswordAuthenticationToken) authentication;
                    // Ajouter les détails d'authentification
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Mettre à jour le contexte de sécurité avec l'authentification
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    // Récupère le token JWT de la requête
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);  // Retirer "Bearer " du token
        }
        return null;
    }
}
