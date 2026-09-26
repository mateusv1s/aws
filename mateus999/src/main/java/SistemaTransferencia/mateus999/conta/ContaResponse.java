package SistemaTransferencia.mateus999.conta;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ContaResponse(
        UUID id,
        String numeroConta,
        String titular,
        BigDecimal saldo,
        StatusConta status,
        Instant createdAt,
        Instant updatedAt
) {
    public static ContaResponse from(Conta conta) {
        return new ContaResponse(
                conta.getId(),
                conta.getNumeroConta(),
                conta.getTitular(),
                conta.getSaldo(),
                conta.getStatus(),
                conta.getCreatedAt(),
                conta.getUpdatedAt()
        );
    }
}
