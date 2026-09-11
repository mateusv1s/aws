package SistemaTransferencia.mateus999.exception;

import java.util.UUID;

public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(UUID contaOrigemId) {
        super("Saldo insuficiente na conta de origem: " + contaOrigemId);
    }
}
