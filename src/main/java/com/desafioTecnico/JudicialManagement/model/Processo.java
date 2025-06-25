package com.desafioTecnico.JudicialManagement.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Processo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Pattern(regexp = "^\\d{7}-\\d{2}\\.\\d{4}\\.\\d{1}\\.\\d{2}\\.\\d{4}$", message = "O número do processo deve seguir o formato 0000000-00.0000.0.00.0000")
    @NotBlank(message = "Número do processo é obrigatório")
    private String numeroProcesso;

    @Column(nullable = false)
    @NotBlank(message = "Vara é obrigatória")
    private String vara;

    @Column(nullable = false)
    @NotBlank(message = "Comarca é obrigatória")
    private String comarca;

    private String assunto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusProcesso status;

    @OneToMany(mappedBy = "processo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Audiencia> audiencias;
}
