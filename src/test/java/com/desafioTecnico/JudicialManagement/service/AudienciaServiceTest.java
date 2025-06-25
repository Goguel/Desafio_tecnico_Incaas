package com.desafioTecnico.JudicialManagement.service;

import com.desafioTecnico.JudicialManagement.dto.AudienciaRequestDTO;
import com.desafioTecnico.JudicialManagement.dto.AudienciaResponseDTO;
import com.desafioTecnico.JudicialManagement.exception.RegraNegocioException;
import com.desafioTecnico.JudicialManagement.model.Audiencia;
import com.desafioTecnico.JudicialManagement.model.Processo;
import com.desafioTecnico.JudicialManagement.model.StatusProcesso;
import com.desafioTecnico.JudicialManagement.model.TipoAudiencia;
import com.desafioTecnico.JudicialManagement.repository.AudienciaRepository;
import com.desafioTecnico.JudicialManagement.repository.ProcessoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AudienciaServiceTest {

    @Mock
    private ProcessoRepository processoRepository;

    @Mock
    private AudienciaRepository audienciaRepository;

    @InjectMocks
    private AudienciaService audienciaService;


    @Test
    @DisplayName("Deve agendar uma audiência com sucesso quando todas as regras são atendidas")
    void deveAgendarAudiencia_QuandoTudoEstiverCorreto() {
        // Arrange (Preparação)
        long processoId = 1L;
        LocalDateTime proximaSegunda = LocalDateTime.now().with(DayOfWeek.MONDAY).plusWeeks(1).withHour(10).withMinute(0);

        AudienciaRequestDTO requestDTO = new AudienciaRequestDTO(
                proximaSegunda,
                TipoAudiencia.INSTRUCAO,
                "Sala 1"
        );

        Processo processoAtivo = new Processo();
        processoAtivo.setId(processoId);
        processoAtivo.setStatus(StatusProcesso.ATIVO);
        processoAtivo.setVara("1ª Vara");

        // O save do repositório ainda trabalha com a entidade Audiencia
        Audiencia audienciaSalva = new Audiencia();
        audienciaSalva.setId(100L); // Simula o ID gerado pelo banco
        audienciaSalva.setProcesso(processoAtivo);
        audienciaSalva.setDataHora(requestDTO.dataHora());
        audienciaSalva.setTipoAudiencia(requestDTO.tipoAudiencia());
        audienciaSalva.setLocal(requestDTO.local());

        // Configura os mocks
        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processoAtivo));
        when(audienciaRepository.existsByProcesso_VaraAndLocalAndDataHora(anyString(), anyString(), any(LocalDateTime.class))).thenReturn(false);
        when(audienciaRepository.save(any(Audiencia.class))).thenReturn(audienciaSalva);

        // Act (Ação)
        AudienciaResponseDTO responseDTO = audienciaService.agendarAudiencia(processoId, requestDTO);

        // Assert (Verificação)
        assertNotNull(responseDTO);
        assertEquals(100L, responseDTO.id()); // Verifica se o DTO de resposta tem os dados corretos
        assertEquals("Sala 1", responseDTO.local());
        verify(audienciaRepository, times(1)).save(any(Audiencia.class));
    }

    // --- Testes das Regras de Negócio (Caminhos de Exceção) ---

    @Test
    @DisplayName("Não deve agendar audiência para um processo ARQUIVADO")
    void naoDeveAgendar_QuandoProcessoArquivado() {
        // Arrange
        long processoId = 1L;
        Processo processoArquivado = new Processo();
        processoArquivado.setId(processoId);
        processoArquivado.setStatus(StatusProcesso.ARQUIVADO);

        AudienciaRequestDTO dto = new AudienciaRequestDTO(LocalDateTime.now().plusDays(1), TipoAudiencia.JULGAMENTO, "Local");

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processoArquivado));

        // Act & Assert
        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            audienciaService.agendarAudiencia(processoId, dto);
        });

        assertEquals("Não é possível agendar audiências para processos arquivados ou suspensos.", exception.getMessage());
        verify(audienciaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve agendar audiência para um processo SUSPENSO")
    void naoDeveAgendar_QuandoProcessoSuspenso() {
        // Arrange (Preparação)
        long processoId = 1L;
        Processo processoSuspenso = new Processo();
        processoSuspenso.setId(processoId);
        // A única diferença do teste anterior: o status é SUSPENSO
        processoSuspenso.setStatus(StatusProcesso.SUSPENSO);

        AudienciaRequestDTO dto = new AudienciaRequestDTO(LocalDateTime.now().plusDays(5), TipoAudiencia.CONCILIACAO, "Online");

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processoSuspenso));

        // Act & Assert
        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            audienciaService.agendarAudiencia(processoId, dto);
        });

        assertEquals("Não é possível agendar audiências para processos arquivados ou suspensos.", exception.getMessage());

        verify(audienciaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Não deve agendar audiência em um SÁBADO")
    void naoDeveAgendar_QuandoDiaForSabado() {
        // Arrange
        long processoId = 1L;
        Processo processoAtivo = new Processo();
        processoAtivo.setId(processoId);
        processoAtivo.setStatus(StatusProcesso.ATIVO);

        LocalDateTime proximoSabado = LocalDateTime.now().with(DayOfWeek.SATURDAY).plusWeeks(1);
        AudienciaRequestDTO dtoNoSabado = new AudienciaRequestDTO(proximoSabado, TipoAudiencia.CONCILIACAO, "Online");

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processoAtivo));

        // Act & Assert
        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            audienciaService.agendarAudiencia(processoId, dtoNoSabado);
        });

        assertEquals("Audiências só podem ser marcadas em dias úteis.", exception.getMessage());
    }

    @Test
    @DisplayName("Não deve agendar audiência se já existir outra no mesmo local e horário (sobreposição)")
    void naoDeveAgendar_QuandoHouverSobreposicao() {
        // Arrange
        long processoId = 1L;
        Processo processoAtivo = new Processo();
        processoAtivo.setId(processoId);
        processoAtivo.setStatus(StatusProcesso.ATIVO);
        processoAtivo.setVara("1ª Vara");

        LocalDateTime proximaTerca = LocalDateTime.now().with(DayOfWeek.TUESDAY).plusWeeks(1).withHour(14);
        AudienciaRequestDTO dto = new AudienciaRequestDTO(proximaTerca, TipoAudiencia.JULGAMENTO, "Sala 2");

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processoAtivo));
        when(audienciaRepository.existsByProcesso_VaraAndLocalAndDataHora("1ª Vara", "Sala 2", proximaTerca)).thenReturn(true);

        // Act & Assert
        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            audienciaService.agendarAudiencia(processoId, dto);
        });

        assertEquals("Já existe uma audiência agendada para esta vara, local, data e hora.", exception.getMessage());
    }
}