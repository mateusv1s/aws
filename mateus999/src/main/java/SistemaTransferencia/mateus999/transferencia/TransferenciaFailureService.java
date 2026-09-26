package SistemaTransferencia.mateus999.transferencia;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferenciaFailureService {

    private final TransferenciaRepository transferenciaRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registrarFalha(UUID transferenciaId, Throwable throwable) {
        transferenciaRepository.findByIdWithLock(transferenciaId).ifPresent(transferencia -> {
            if (transferencia.getStatus() == StatusTransferencia.CONCLUIDA ||
                    transferencia.getStatus() == StatusTransferencia.ERRO_DLQ) {
                return;
            }

            transferencia.setTentativas(transferencia.getTentativas() + 1);
            transferencia.setStatus(StatusTransferencia.ERRO_REPROCESSAVEL);
            String message = throwable.getMessage();
            if (message == null || message.isBlank()) {
                message = throwable.getClass().getSimpleName();
            }
            transferencia.setUltimoErro(message.length() > 1000 ? message.substring(0, 1000) : message);
        });
    }
}
