package SistemaTransferencia.mateus999.conta;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContaRepository extends JpaRepository<Conta, UUID> {

    boolean existsByNumeroConta(String numeroConta);

    List<Conta> findAllByUsuarioEmailIgnoreCaseOrderByCreatedAtDesc(String email);

    Optional<Conta> findByIdAndUsuarioEmailIgnoreCase(UUID id, String email);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Conta c where c.id = :id")
    Optional<Conta> findByIdWithLock(@Param("id") UUID id);
}
