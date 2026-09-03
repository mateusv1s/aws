package SistemaTransferencia.mateus999.repository;

import SistemaTransferencia.mateus999.entity.Transferencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TransferenciaRepository extends JpaRepository <Transferencia, UUID> {
    Optional<Transferencia> findByIdempotencyKey(String idempotencyKey);
}
