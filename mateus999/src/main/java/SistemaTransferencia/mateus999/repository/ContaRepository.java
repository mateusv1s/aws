package SistemaTransferencia.mateus999.repository;

import SistemaTransferencia.mateus999.entity.Conta;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ContaRepository extends JpaRepository<Conta, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select c from Conta c where c.id = :id")
    Optional<Conta> buscarParaAtualizar(@Param("id") UUID id);
}
