package SistemaTransferencia.mateus999.transferencia;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransferenciaRepository extends JpaRepository<Transferencia, UUID> {

    Optional<Transferencia> findByUsuarioSolicitanteEmailIgnoreCaseAndIdempotencyKey(String email, String idempotencyKey);

    List<Transferencia> findAllByUsuarioSolicitanteEmailIgnoreCaseOrderByCreatedAtDesc(String email);

    Optional<Transferencia> findByIdAndUsuarioSolicitanteEmailIgnoreCase(UUID id, String email);

    List<Transferencia> findTop50ByStatusInAndUpdatedAtBeforeOrderByUpdatedAtAsc(
            Collection<StatusTransferencia> statuses,
            Instant before
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from Transferencia t where t.id = :id")
    Optional<Transferencia> findByIdWithLock(@Param("id") UUID id);
}
