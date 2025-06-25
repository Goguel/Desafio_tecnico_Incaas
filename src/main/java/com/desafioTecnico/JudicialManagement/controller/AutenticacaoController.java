package com.desafioTecnico.JudicialManagement.controller;

import com.desafioTecnico.JudicialManagement.dto.LoginRequestDTO;
import com.desafioTecnico.JudicialManagement.dto.TokenResponseDTO;
import com.desafioTecnico.JudicialManagement.service.TokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Autenticação do usuário")
public class AutenticacaoController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @PostMapping
    public ResponseEntity<TokenResponseDTO> login(@RequestBody LoginRequestDTO loginDTO) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDTO.login(), loginDTO.senha());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        String token = tokenService.gerarToken(authentication);
        return ResponseEntity.ok(new TokenResponseDTO(token));
    }
}