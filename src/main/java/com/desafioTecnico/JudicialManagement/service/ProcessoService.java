package com.desafioTecnico.JudicialManagement.service;

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

    public Processo criarProcesso(Processo processo) {
        // Validações adicionais, se necessário, podem ser feitas aqui
        return processoRepository.save(processo);
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