package SistemaTransferencia.mateus999.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CriarContaRequest(
        @NotBlank String nome,
        @PositiveOrZero BigDecimal saldoInicial
) {}
