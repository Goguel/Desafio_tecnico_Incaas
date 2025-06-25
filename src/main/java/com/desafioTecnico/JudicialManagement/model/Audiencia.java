package com.desafioTecnico.JudicialManagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Audiencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "processo_id", nullable = false)
    private Processo processo;

    @Column(nullable = false)
    @NotNull(message = "Data e hora são obrigatórias")
    @Future(message = "A audiência deve ser agendada para uma data futura")
    private LocalDateTime dataHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "Tipo de audiência é obrigatório")
    private TipoAudiencia tipoAudiencia;

    @Column(nullable = false)
    @NotBlank(message = "Local é obrigatório")
    private String local;
}