package SistemaTransferencia.mateus999.exception;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class ContaNaoEncontradaException extends RuntimeException {
    public ContaNaoEncontradaException(@NotNull UUID message) {
    }
}
