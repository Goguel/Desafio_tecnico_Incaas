package com.desafioTecnico.JudicialManagement.repository;

import com.desafioTecnico.JudicialManagement.model.Processo;
import com.desafioTecnico.JudicialManagement.model.StatusProcesso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProcessoRepository extends JpaRepository<Processo, Long> {

    List<Processo> findByStatus(StatusProcesso status);
    List<Processo> findByComarca(String comarca);
    List<Processo> findByStatusAndComarca(StatusProcesso status, String comarca);
}
