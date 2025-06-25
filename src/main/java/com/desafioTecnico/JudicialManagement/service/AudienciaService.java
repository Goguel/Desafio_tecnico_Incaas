package com.desafioTecnico.JudicialManagement.service;

import com.desafioTecnico.JudicialManagement.exception.RegraNegocioException;
import com.desafioTecnico.JudicialManagement.model.Audiencia;
import com.desafioTecnico.JudicialManagement.model.Processo;
import com.desafioTecnico.JudicialManagement.model.StatusProcesso;
import com.desafioTecnico.JudicialManagement.repository.AudienciaRepository;
import com.desafioTecnico.JudicialManagement.repository.ProcessoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AudienciaService {

    private final AudienciaRepository audienciaRepository;
    private final ProcessoRepository processoRepository;

    public Audiencia agendarAudiencia(Long processoId, Audiencia audiencia) {
        Processo processo = processoRepository.findById(processoId)
                .orElseThrow(() -> new RegraNegocioException("Processo não encontrado."));

        // Regra: Não agendar para processos arquivados ou suspensos
        if (processo.getStatus() == StatusProcesso.ARQUIVADO || processo.getStatus() == StatusProcesso.SUSPENSO) {
            throw new RegraNegocioException("Não é possível agendar audiências para processos arquivados ou suspensos.");
        }

        LocalDateTime dataHoraAudiencia = audiencia.getDataHora();

        // Regra: Marcar apenas em dias úteis (segunda a sexta)
        DayOfWeek diaDaSemana = dataHoraAudiencia.getDayOfWeek();
        if (diaDaSemana == DayOfWeek.SATURDAY || diaDaSemana == DayOfWeek.SUNDAY) {
            throw new RegraNegocioException("Audiências só podem ser marcadas em dias úteis.");
        }

        // Regra: Não permitir sobreposição de audiências
        boolean sobreposicao = audienciaRepository.existsByProcesso_VaraAndLocalAndDataHora(
                processo.getVara(), audiencia.getLocal(), dataHoraAudiencia);
        if (sobreposicao) {
            throw new RegraNegocioException("Já existe uma audiência agendada para esta vara, local, data e hora.");
        }

        audiencia.setProcesso(processo);
        return audienciaRepository.save(audiencia);
    }

    public List<Audiencia> consultarAgendaDaComarca(String comarca, LocalDate dia) {
        LocalDateTime inicioDoDia = dia.atStartOfDay(); // 2025-06-25T00:00:00
        LocalDateTime fimDoDia = dia.atTime(LocalTime.MAX);   // 2025-06-25T23:59:59.999...
        return audienciaRepository.findByComarcaAndData(comarca, inicioDoDia, fimDoDia);
    }
}