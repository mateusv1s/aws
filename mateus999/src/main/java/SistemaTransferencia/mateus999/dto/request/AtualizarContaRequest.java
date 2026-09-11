package SistemaTransferencia.mateus999.dto.request;

import jakarta.validation.constraints.NotBlank;

public record AtualizarContaRequest(
        @NotBlank String nome
) {}
