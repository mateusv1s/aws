package SistemaTransferencia.mateus999.entity;

import SistemaTransferencia.mateus999.enumerations.StatusTransferencia;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Getter
@Setter
@Entity
public class Transferencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    private String idempotencyKey;

    private UUID contaOrigemId;
    private UUID contaDestinoId;
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    private StatusTransferencia status;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public UUID getContaOrigemId() {
        return contaOrigemId;
    }

    public void setContaOrigemId(UUID contaOrigemId) {
        this.contaOrigemId = contaOrigemId;
    }

    public UUID getContaDestinoId() {
        return contaDestinoId;
    }

    public void setContaDestinoId(UUID contaDestinoId) {
        this.contaDestinoId = contaDestinoId;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public StatusTransferencia getStatus() {
        return status;
    }

    public void setStatus(StatusTransferencia status) {
        this.status = status;
    }
}
