package SistemaTransferencia.mateus999.dto.response;

import SistemaTransferencia.mateus999.entity.Conta;

import java.math.BigDecimal;
import java.util.UUID;

public record ContaResponse(
        UUID id,
        String nome,
        BigDecimal saldo
) {
    public static ContaResponse de(Conta conta) {
        return new ContaResponse(conta.getId(), conta.getNome(), conta.getSaldo());
    }
}
