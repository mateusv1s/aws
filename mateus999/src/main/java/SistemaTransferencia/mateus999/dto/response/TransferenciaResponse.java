package SistemaTransferencia.mateus999.dto.response;

import SistemaTransferencia.mateus999.entity.Transferencia;
import SistemaTransferencia.mateus999.enumerations.StatusTransferencia;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record TransferenciaResponse(
        UUID id,
        UUID contaOrigemId,
        UUID contaDestinoId,
        BigDecimal valor,
        StatusTransferencia status,
        String motivoFalha,
        Instant criadaEm,
        Instant atualizadaEm
) {
    public static TransferenciaResponse de(Transferencia t) {
        return new TransferenciaResponse(
                t.getId(), t.getContaOrigemId(), t.getContaDestinoId(), t.getValor(),
                t.getStatus(), t.getMotivoFalha(), t.getCriadaEm(), t.getAtualizadaEm()
        );
    }
}
