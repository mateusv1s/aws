package SistemaTransferencia.mateus999.dto.response;

import java.time.Instant;

public record ErroResponse(
        int status,
        String mensagem,
        Instant timestamp
) {
    public static ErroResponse de(int status, String mensagem) {
        return new ErroResponse(status, mensagem, Instant.now());
    }
}
