package com.desafioTecnico.JudicialManagement.controller;

import com.desafioTecnico.JudicialManagement.dto.AgendaResponseDTO;
import com.desafioTecnico.JudicialManagement.dto.AudienciaRequestDTO;
import com.desafioTecnico.JudicialManagement.dto.AudienciaResponseDTO;
import com.desafioTecnico.JudicialManagement.model.Audiencia;
import com.desafioTecnico.JudicialManagement.service.AudienciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/audiencias")
@RequiredArgsConstructor
@Tag(name = "Audiências", description = "Agendamento e consulta de audiências")
public class AudienciaController {

    private final AudienciaService audienciaService;

    @PostMapping("/processo/{processoId}")
    @Operation(summary = "Agenda uma nova audiência para um processo")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<AudienciaResponseDTO> agendarAudiencia(
            @PathVariable Long processoId,
            @Valid @RequestBody AudienciaRequestDTO dto) { // Recebe o DTO
        AudienciaResponseDTO novaAudiencia = audienciaService.agendarAudiencia(processoId, dto);
        return new ResponseEntity<>(novaAudiencia, HttpStatus.CREATED); // Retorna o DTO
    }

    @GetMapping("/agenda")
    @Operation(summary = "Consulta a agenda de audiências de uma comarca em um dia específico")
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<List<AgendaResponseDTO>> consultarAgenda( // Retorna uma lista do DTO de agenda
                                                                    @RequestParam String comarca,
                                                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dia) {
        List<AgendaResponseDTO> agenda = audienciaService.consultarAgendaDaComarca(comarca, dia);
        return ResponseEntity.ok(agenda);
    }
}