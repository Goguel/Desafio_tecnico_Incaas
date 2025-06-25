package com.desafioTecnico.JudicialManagement.dto;

import com.desafioTecnico.JudicialManagement.model.TipoAudiencia;

import java.time.LocalDateTime;

public record AudienciaResponseDTO(
        Long id,
        LocalDateTime dataHora,
        TipoAudiencia tipoAudiencia,
        String local
) {}