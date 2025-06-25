package com.desafioTecnico.JudicialManagement.service;

import com.desafioTecnico.JudicialManagement.dto.ProcessoRequestDTO;
import com.desafioTecnico.JudicialManagement.dto.ProcessoResponseDTO;
import com.desafioTecnico.JudicialManagement.model.Processo;
import com.desafioTecnico.JudicialManagement.model.StatusProcesso;
import com.desafioTecnico.JudicialManagement.repository.ProcessoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProcessoService {

    private final ProcessoRepository processoRepository;

    public ProcessoResponseDTO criarProcesso(ProcessoRequestDTO dto) {
        // 1. Converter DTO para Entidade
        Processo processo = new Processo();
        processo.setNumeroProcesso(dto.numeroProcesso());
        processo.setVara(dto.vara());
        processo.setComarca(dto.comarca());
        processo.setAssunto(dto.assunto());
        processo.setStatus(dto.status());

        Processo processoSalvo = processoRepository.save(processo);

        // 2. Converter Entidade para DTO de resposta
        return new ProcessoResponseDTO(
                processoSalvo.getId(),
                processoSalvo.getNumeroProcesso(),
                processoSalvo.getVara(),
                processoSalvo.getComarca(),
                processoSalvo.getAssunto(),
                processoSalvo.getStatus()
        );
    }

    public List<Processo> listarProcessos(StatusProcesso status, String comarca) {
        if (status != null && comarca != null) {
            return processoRepository.findByStatusAndComarca(status, comarca);
        } else if (status != null) {
            return processoRepository.findByStatus(status);
        } else if (comarca != null) {
            return processoRepository.findByComarca(comarca);
        } else {
            return processoRepository.findAll();
        }
    }
}