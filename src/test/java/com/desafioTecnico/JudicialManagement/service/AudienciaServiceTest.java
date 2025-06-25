package com.desafioTecnico.JudicialManagement.service;

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
        Processo processoAtivo = new Processo();
        processoAtivo.setId(processoId);
        processoAtivo.setStatus(StatusProcesso.ATIVO);
        processoAtivo.setVara("1ª Vara");

        Audiencia novaAudiencia = new Audiencia();
        // Garante que a data seja em um dia útil futuro (ex: próxima segunda-feira às 10h)
        LocalDateTime proximaSegunda = LocalDateTime.now().with(DayOfWeek.MONDAY).plusWeeks(1).withHour(10).withMinute(0);
        novaAudiencia.setDataHora(proximaSegunda);
        novaAudiencia.setLocal("Sala 1");
        novaAudiencia.setTipoAudiencia(TipoAudiencia.INSTRUCAO);

        // Configura os mocks
        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processoAtivo));
        when(audienciaRepository.existsByProcesso_VaraAndLocalAndDataHora(anyString(), anyString(), any(LocalDateTime.class))).thenReturn(false);
        // Quando o save for chamado, retorna a própria audiência para verificação
        when(audienciaRepository.save(any(Audiencia.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act (Ação)
        Audiencia audienciaAgendada = audienciaService.agendarAudiencia(processoId, novaAudiencia);

        // Assert (Verificação)
        assertNotNull(audienciaAgendada);
        assertEquals(processoAtivo, audienciaAgendada.getProcesso());
        verify(audienciaRepository, times(1)).save(novaAudiencia); // Verifica se o save foi chamado exatamente 1 vez.
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

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processoArquivado));

        // Act & Assert
        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            audienciaService.agendarAudiencia(processoId, new Audiencia());
        });

        assertEquals("Não é possível agendar audiências para processos arquivados ou suspensos.", exception.getMessage());
        verify(audienciaRepository, never()).save(any()); // Garante que NUNCA tentamos salvar
    }

    @Test
    @DisplayName("Não deve agendar audiência para um processo SUSPENSO")
    void naoDeveAgendar_QuandoProcessoSuspenso() {
        // Arrange
        long processoId = 1L;
        Processo processoSuspenso = new Processo();
        processoSuspenso.setId(processoId);
        processoSuspenso.setStatus(StatusProcesso.SUSPENSO);

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processoSuspenso));

        // Act & Assert
        assertThrows(RegraNegocioException.class, () -> {
            audienciaService.agendarAudiencia(processoId, new Audiencia());
        });
    }

    @Test
    @DisplayName("Não deve agendar audiência em um SÁBADO")
    void naoDeveAgendar_QuandoDiaForSabado() {
        // Arrange
        long processoId = 1L;
        Processo processoAtivo = new Processo();
        processoAtivo.setId(processoId);
        processoAtivo.setStatus(StatusProcesso.ATIVO);

        Audiencia audienciaNoSabado = new Audiencia();
        LocalDateTime proximoSabado = LocalDateTime.now().with(DayOfWeek.SATURDAY).plusWeeks(1);
        audienciaNoSabado.setDataHora(proximoSabado);

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processoAtivo));

        // Act & Assert
        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            audienciaService.agendarAudiencia(processoId, audienciaNoSabado);
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

        Audiencia novaAudiencia = new Audiencia();
        LocalDateTime proximaTerca = LocalDateTime.now().with(DayOfWeek.TUESDAY).plusWeeks(1).withHour(14);
        novaAudiencia.setDataHora(proximaTerca);
        novaAudiencia.setLocal("Sala 2");

        when(processoRepository.findById(processoId)).thenReturn(Optional.of(processoAtivo));
        // Simula que a query de verificação de existência retornou 'true'
        when(audienciaRepository.existsByProcesso_VaraAndLocalAndDataHora("1ª Vara", "Sala 2", proximaTerca)).thenReturn(true);

        // Act & Assert
        RegraNegocioException exception = assertThrows(RegraNegocioException.class, () -> {
            audienciaService.agendarAudiencia(processoId, novaAudiencia);
        });

        assertEquals("Já existe uma audiência agendada para esta vara, local, data e hora.", exception.getMessage());
    }
}