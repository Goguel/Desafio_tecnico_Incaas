package com.desafioTecnico.JudicialManagement.controller;

import com.desafioTecnico.JudicialManagement.dto.ProcessoRequestDTO;
import com.desafioTecnico.JudicialManagement.dto.ProcessoResponseDTO;
import com.desafioTecnico.JudicialManagement.model.Processo;
import com.desafioTecnico.JudicialManagement.model.StatusProcesso;
import com.desafioTecnico.JudicialManagement.service.ProcessoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/processos")
@RequiredArgsConstructor
@Tag(name = "Processos", description = "Gerenciamento de Processos Judiciais")
public class ProcessoController {

    private final ProcessoService processoService;

    @PostMapping
    @Operation(summary = "Cria um novo processo judicial")
    public ResponseEntity<ProcessoResponseDTO> criarProcesso(@Valid @RequestBody ProcessoRequestDTO processoDTO) {
        ProcessoResponseDTO novoProcesso = processoService.criarProcesso(processoDTO);
        return new ResponseEntity<>(novoProcesso, HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Lista todos os processos com filtros opcionais")
    public ResponseEntity<List<Processo>> listarProcessos(
            @RequestParam(required = false) StatusProcesso status,
            @RequestParam(required = false) String comarca) {
        List<Processo> processos = processoService.listarProcessos(status, comarca);
        return ResponseEntity.ok(processos);
    }
}