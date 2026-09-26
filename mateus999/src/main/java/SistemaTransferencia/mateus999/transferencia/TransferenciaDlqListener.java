package SistemaTransferencia.mateus999.transferencia;

import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransferenciaDlqListener {

    private final TransferenciaDlqService dlqService;

    @SqsListener("${app.sqs.transfer-dlq}")
    public void consumirDlq(TransferenciaEvent event) {
        dlqService.marcarComoDlq(event.transferenciaId());
    }
}
