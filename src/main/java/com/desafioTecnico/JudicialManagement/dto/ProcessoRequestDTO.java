package com.desafioTecnico.JudicialManagement.dto;

import com.desafioTecnico.JudicialManagement.model.StatusProcesso;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record ProcessoRequestDTO(
        @Pattern(regexp = "^\\d{7}-\\d{2}\\.\\d{4}\\.\\d{1}\\.\\d{2}\\.\\d{4}$", message = "...")
        @NotBlank
        String numeroProcesso,

        @NotBlank
        String vara,

        @NotBlank
        String comarca,

        String assunto,

        @NotNull
        StatusProcesso status
) {}
