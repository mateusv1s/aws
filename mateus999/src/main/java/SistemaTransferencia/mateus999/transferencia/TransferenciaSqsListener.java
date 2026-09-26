package SistemaTransferencia.mateus999.transferencia;

import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TransferenciaSqsListener {

    private final TransferenciaProcessor processor;
    private final TransferenciaFailureService failureService;

    @SqsListener("${app.sqs.transfer-queue}")
    public void consumir(TransferenciaEvent event) {
        try {
            processor.processar(event.transferenciaId());
        } catch (RuntimeException ex) {
            failureService.registrarFalha(event.transferenciaId(), ex);
            throw ex;
        }
    }
}
