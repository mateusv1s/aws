package SistemaTransferencia.mateus999.transferencia;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferenciaResponse(
        UUID id,
        UUID contaOrigemId,
        UUID contaDestinoId,
        BigDecimal valor,
        StatusTransferencia status,
        String idempotencyKey,
        int tentativas,
        String ultimoErro,
        Instant createdAt,
        Instant updatedAt,
        Instant processedAt
) {
    public static TransferenciaResponse from(Transferencia transferencia) {
        return new TransferenciaResponse(
                transferencia.getId(),
                transferencia.getContaOrigem().getId(),
                transferencia.getContaDestino().getId(),
                transferencia.getValor(),
                transferencia.getStatus(),
                transferencia.getIdempotencyKey(),
                transferencia.getTentativas(),
                transferencia.getUltimoErro(),
                transferencia.getCreatedAt(),
                transferencia.getUpdatedAt(),
                transferencia.getProcessedAt()
        );
    }
}
