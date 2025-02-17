package com.example.locationvehicule.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

    private String jwtSecret = "your-secret-key";  // Remplace par une clé secrète plus sécurisée
    private int jwtExpirationInMs = 604800000; // 1 semaine

    // Générer le token JWT à partir de l'authentification
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // Extraire les informations d'authentification du token JWT
    public Authentication getAuthentication(String token) {
        Claims claims = getClaimsFromToken(token);
        String username = claims.getSubject();

        // Crée un objet Authentication avec les détails de l'utilisateur
        // (Ici, tu peux étendre pour inclure les rôles et autres)
        return new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(username, null, null);
    }

    // Extraire les informations de revendication (Claims) du token
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody();
    }

    // Valider le token JWT
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaimsFromToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
