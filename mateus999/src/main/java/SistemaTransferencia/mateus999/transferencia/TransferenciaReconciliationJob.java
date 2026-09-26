package SistemaTransferencia.mateus999.transferencia;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TransferenciaReconciliationJob {

    private final TransferenciaRepository transferenciaRepository;
    private final TransferenciaRecoveryService recoveryService;

    @Value("${app.reconciliation.stale-after}")
    private Duration staleAfter;

    @Value("${app.reconciliation.max-attempts}")
    private int maxAttempts;

    @Scheduled(fixedDelayString = "${app.reconciliation.fixed-delay-ms}")
    public void reconciliar() {
        Instant limite = Instant.now().minus(staleAfter);
        List<Transferencia> antigas = transferenciaRepository
                .findTop50ByStatusInAndUpdatedAtBeforeOrderByUpdatedAtAsc(
                        List.of(
                                StatusTransferencia.PENDENTE,
                                StatusTransferencia.PROCESSANDO,
                                StatusTransferencia.ERRO_REPROCESSAVEL
                        ),
                        limite
                );

        antigas.forEach(t -> recoveryService.reconciliar(t.getId(), maxAttempts));
    }
}
