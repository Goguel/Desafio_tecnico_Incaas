package com.desafioTecnico.JudicialManagement.repository;

import com.desafioTecnico.JudicialManagement.model.Audiencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AudienciaRepository extends JpaRepository<Audiencia, Long> {

    @Query("SELECT COUNT(a) > 0 FROM Audiencia a WHERE a.processo.vara = :vara AND a.local = :local AND a.dataHora = :dataHora")
    boolean existsByProcesso_VaraAndLocalAndDataHora(String vara, String local, LocalDateTime dataHora);

    @Query("SELECT a FROM Audiencia a WHERE a.processo.comarca = :comarca AND a.dataHora >= :inicioDoDia AND a.dataHora <= :fimDoDia")
    List<Audiencia> findByProcesso_ComarcaAndDataHoraBetween(String comarca, LocalDateTime inicioDoDia, LocalDateTime fimDoDia);
}