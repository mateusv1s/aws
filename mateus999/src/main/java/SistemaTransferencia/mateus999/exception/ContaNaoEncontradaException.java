package SistemaTransferencia.mateus999.exception;

import java.util.UUID;

public class ContaNaoEncontradaException extends RuntimeException {
    public ContaNaoEncontradaException(UUID id) {
        super("Conta nao encontrada: " + id);
    }
}
