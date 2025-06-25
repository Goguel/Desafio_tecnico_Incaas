package com.desafioTecnico.JudicialManagement.service;

import com.desafioTecnico.JudicialManagement.model.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.Date;
import javax.crypto.SecretKey;

@Service
public class TokenService {

    @Value("${api.security.jwt.expiration-ms}")
    private long expiration;

    @Value("${api.security.jwt.secret}")
    private String secret;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String gerarToken(Authentication authentication) {
        UserDetails principal = (UserDetails) authentication.getPrincipal();

        // Agora, não importa se o objeto é um 'User' do Spring ou um 'Usuario' do projeto,
        // ambos têm o método getUsername() por causa da interface.
        String username = principal.getUsername();

        Date hoje = new Date();
        Date dataExpiracao = new Date(hoje.getTime() + expiration);

        return Jwts.builder()
                .setIssuer("API Gerenciador de Processos")
                .setSubject(username)
                .setIssuedAt(hoje)
                .setExpiration(dataExpiracao)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValido(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
        return claims.getSubject();
    }
}