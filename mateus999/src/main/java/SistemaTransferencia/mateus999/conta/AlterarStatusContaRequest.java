package SistemaTransferencia.mateus999.conta;

import jakarta.validation.constraints.NotNull;

public record AlterarStatusContaRequest(@NotNull StatusConta status) {
}
