package SistemaTransferencia.mateus999.entity;

import SistemaTransferencia.mateus999.enumerations.StatusTransferencia;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
public class Transferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String idempotencyKey;

    private UUID contaOrigemId;
    private UUID contaDestinoId;
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    private StatusTransferencia status;

    private String motivoFalha;

    @CreationTimestamp
    private Instant criadaEm;

    @UpdateTimestamp
    private Instant atualizadaEm;
}
