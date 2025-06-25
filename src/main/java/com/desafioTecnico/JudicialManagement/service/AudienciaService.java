package com.desafioTecnico.JudicialManagement.service;

import com.desafioTecnico.JudicialManagement.dto.AgendaResponseDTO;
import com.desafioTecnico.JudicialManagement.dto.AudienciaRequestDTO;
import com.desafioTecnico.JudicialManagement.dto.AudienciaResponseDTO;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AudienciaService {

    private final AudienciaRepository audienciaRepository;
    private final ProcessoRepository processoRepository;

    public AudienciaResponseDTO agendarAudiencia(Long processoId, AudienciaRequestDTO dto) {
        Processo processo = processoRepository.findById(processoId)
                .orElseThrow(() -> new RegraNegocioException("Processo não encontrado."));

        // As regras de negócio continuam as mesmas
        if (processo.getStatus() == StatusProcesso.ARQUIVADO || processo.getStatus() == StatusProcesso.SUSPENSO) {
            throw new RegraNegocioException("Não é possível agendar audiências para processos arquivados ou suspensos.");
        }

        LocalDateTime dataHoraAudiencia = dto.dataHora();

        DayOfWeek diaDaSemana = dataHoraAudiencia.getDayOfWeek();
        if (diaDaSemana == DayOfWeek.SATURDAY || diaDaSemana == DayOfWeek.SUNDAY) {
            throw new RegraNegocioException("Audiências só podem ser marcadas em dias úteis.");
        }

        boolean sobreposicao = audienciaRepository.existsByProcesso_VaraAndLocalAndDataHora(
                processo.getVara(), dto.local(), dataHoraAudiencia);
        if (sobreposicao) {
            throw new RegraNegocioException("Já existe uma audiência agendada para esta vara, local, data e hora.");
        }

        // Mapeamento: DTO -> Entidade
        Audiencia novaAudiencia = new Audiencia();
        novaAudiencia.setProcesso(processo);
        novaAudiencia.setDataHora(dto.dataHora());
        novaAudiencia.setTipoAudiencia(dto.tipoAudiencia());
        novaAudiencia.setLocal(dto.local());

        Audiencia audienciaSalva = audienciaRepository.save(novaAudiencia);

        // Mapeamento: Entidade -> DTO de Resposta
        return new AudienciaResponseDTO(
                audienciaSalva.getId(),
                audienciaSalva.getDataHora(),
                audienciaSalva.getTipoAudiencia(),
                audienciaSalva.getLocal()
        );
    }

    public List<AgendaResponseDTO> consultarAgendaDaComarca(String comarca, LocalDate dia) {
        LocalDateTime inicioDoDia = dia.atStartOfDay();
        LocalDateTime fimDoDia = dia.atTime(LocalTime.MAX);

        List<Audiencia> audiencias = audienciaRepository.findByProcesso_ComarcaAndDataHoraBetween(comarca, inicioDoDia, fimDoDia);

        // Mapeamento: Lista de Entidades -> Lista de DTOs de Resposta
        return audiencias.stream()
                .map(audiencia -> new AgendaResponseDTO(
                        audiencia.getId(),
                        audiencia.getDataHora(),
                        audiencia.getTipoAudiencia(),
                        audiencia.getLocal(),
                        new AgendaResponseDTO.ProcessoInfoDTO(
                                audiencia.getProcesso().getNumeroProcesso(),
                                audiencia.getProcesso().getVara()
                        )
                ))
                .collect(Collectors.toList());
    }
}