package com.desafioTecnico.JudicialManagement.dto;

import com.desafioTecnico.JudicialManagement.model.TipoAudiencia;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record AudienciaRequestDTO(
        @NotNull(message = "Data e hora são obrigatórias")
        @Future(message = "A audiência deve ser agendada para uma data futura")
        LocalDateTime dataHora,

        @NotNull(message = "Tipo de audiência é obrigatório")
        TipoAudiencia tipoAudiencia,

        @NotBlank(message = "Local é obrigatório")
        String local
) {}
