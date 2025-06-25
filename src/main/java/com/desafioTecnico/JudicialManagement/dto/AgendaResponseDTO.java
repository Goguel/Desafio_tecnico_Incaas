package com.desafioTecnico.JudicialManagement.dto;

import com.desafioTecnico.JudicialManagement.model.TipoAudiencia;

import java.time.LocalDateTime;

public record AgendaResponseDTO(
        Long id,
        LocalDateTime dataHora,
        TipoAudiencia tipoAudiencia,
        String local,
        ProcessoInfoDTO processo
) {
    // DTO aninhado para informações resumidas do processo
    public record ProcessoInfoDTO(String numeroProcesso, String vara) {}
}