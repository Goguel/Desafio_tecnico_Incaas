package com.desafioTecnico.JudicialManagement.controller;

import com.desafioTecnico.JudicialManagement.model.Audiencia;
import com.desafioTecnico.JudicialManagement.service.AudienciaService;
import io.swagger.v3.oas.annotations.Operation;
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
    public ResponseEntity<Audiencia> agendarAudiencia(
            @PathVariable Long processoId,
            @Valid @RequestBody Audiencia audiencia) {
        Audiencia novaAudiencia = audienciaService.agendarAudiencia(processoId, audiencia);
        return new ResponseEntity<>(novaAudiencia, HttpStatus.CREATED);
    }

    @GetMapping("/agenda")
    @Operation(summary = "Consulta a agenda de audiências de uma comarca em um dia específico")
    public ResponseEntity<List<Audiencia>> consultarAgenda(
            @RequestParam String comarca,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dia) {
        List<Audiencia> agenda = audienciaService.consultarAgendaDaComarca(comarca, dia);
        return ResponseEntity.ok(agenda);
    }
}