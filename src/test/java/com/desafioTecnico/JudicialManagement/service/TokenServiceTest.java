package com.desafioTecnico.JudicialManagement.service;

import com.desafioTecnico.JudicialManagement.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        ReflectionTestUtils.setField(tokenService, "expiration", 3600000L); // 1 hora
        ReflectionTestUtils.setField(tokenService, "secret", "minha-chave-secreta-super-longa-e-segura-para-testes-unitarios");
    }

    @Test
    @DisplayName("Deve gerar um token JWT válido e extrair o username corretamente")
    void deveGerarEValidarTokenComSucesso() {
        // Arrange
        Usuario usuario = new Usuario("testuser", "password");
        Authentication authentication = new UsernamePasswordAuthenticationToken(usuario, null);

        // Act
        String token = tokenService.gerarToken(authentication);

        // Assert
        assertNotNull(token);
        assertFalse(token.isBlank());

        // Verifica se o token é válido e se o username extraído é o correto
        boolean isTokenValido = tokenService.isTokenValido(token);
        assertTrue(isTokenValido);

        String usernameExtraido = tokenService.getUsernameFromToken(token);
        assertEquals("testuser", usernameExtraido);
    }

    @Test
    @DisplayName("Deve retornar falso para um token inválido ou malformado")
    void deveInvalidarTokenCorrompido() {
        // Arrange
        String tokenInvalido = "um.token.falso";
        String tokenExpirado = "eyJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJBUEkgR2VyZW5jaWFkb3IgZGUgUHJvY2Vzc29zIiwic3ViIjoidGVzdHVzZXIiLCJpYXQiOjE2MjQ2NjIwMDAsImV4cCI6MTYyNDY2NTYwMH0.123abc"; // Token expirado de exemplo

        // Act & Assert
        assertFalse(tokenService.isTokenValido(tokenInvalido));
        assertFalse(tokenService.isTokenValido(tokenExpirado));
    }
}