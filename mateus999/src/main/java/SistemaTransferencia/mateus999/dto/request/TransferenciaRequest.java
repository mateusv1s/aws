package SistemaTransferencia.mateus999.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferenciaRequest(
        @NotNull UUID contaOrigemId,
        @NotNull UUID contaDestinoId,
        @NotNull @PositiveOrZero BigDecimal valor
) {}
