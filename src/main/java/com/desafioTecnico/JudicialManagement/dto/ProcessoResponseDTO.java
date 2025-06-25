package com.desafioTecnico.JudicialManagement.dto;

import com.desafioTecnico.JudicialManagement.model.StatusProcesso;

public record ProcessoResponseDTO(
        Long id,
        String numeroProcesso,
        String vara,
        String comarca,
        String assunto,
        StatusProcesso status
) {}