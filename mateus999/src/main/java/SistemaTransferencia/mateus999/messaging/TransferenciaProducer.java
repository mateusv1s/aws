package SistemaTransferencia.mateus999.messaging;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TransferenciaProducer {

    private final SqsTemplate sqsTemplate;
    private final String filaTransferencias;

    public TransferenciaProducer(
            SqsTemplate sqsTemplate,
            @Value("${app.sqs.transferencias-queue-name}") String filaTransferencias) {
        this.sqsTemplate = sqsTemplate;
        this.filaTransferencias = filaTransferencias;
    }

    public void enviar(UUID transferenciaId) {
        sqsTemplate.send(to -> to.queue(filaTransferencias)
                .payload(new TransferenciaMessage(transferenciaId)));
    }
}
