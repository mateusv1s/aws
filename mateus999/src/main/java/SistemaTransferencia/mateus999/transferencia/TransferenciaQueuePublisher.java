package SistemaTransferencia.mateus999.transferencia;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TransferenciaQueuePublisher {

    private final SqsTemplate sqsTemplate;

    @Value("${app.sqs.transfer-queue}")
    private String transferQueue;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterCommit(TransferenciaSolicitadaEvent event) {
        publicar(event.transferenciaId());
    }

    public void publicar(UUID transferenciaId) {
        sqsTemplate.send(transferQueue, new TransferenciaEvent(transferenciaId));
    }
}
