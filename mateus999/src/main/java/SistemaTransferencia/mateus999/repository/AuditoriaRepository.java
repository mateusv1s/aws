package SistemaTransferencia.mateus999.repository;

import SistemaTransferencia.mateus999.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditoriaRepository extends JpaRepository<Auditoria, UUID> {
    List<Auditoria> findByTransferenciaIdOrderByCriadaEmAsc(UUID transferenciaId);
}
