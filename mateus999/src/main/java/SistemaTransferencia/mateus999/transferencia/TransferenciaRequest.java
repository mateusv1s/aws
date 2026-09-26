package SistemaTransferencia.mateus999.transferencia;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferenciaRequest(
        @NotNull UUID contaOrigemId,
        @NotNull UUID contaDestinoId,
        @NotNull @DecimalMin("0.01") @Digits(integer = 17, fraction = 2) BigDecimal valor
) {
}
