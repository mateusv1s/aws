package SistemaTransferencia.mateus999.transferencia;

import SistemaTransferencia.mateus999.exception.BusinessException;
import SistemaTransferencia.mateus999.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferenciaRecoveryService {

    private final TransferenciaRepository transferenciaRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public TransferenciaResponse reprocessarManual(UUID id) {
        Transferencia transferencia = transferenciaRepository.findByIdWithLock(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transferencia nao encontrada"));

        if (transferencia.getStatus() == StatusTransferencia.CONCLUIDA) {
            throw new BusinessException("Transferencia ja concluida");
        }
        if (transferencia.getStatus() != StatusTransferencia.ERRO_DLQ) {
            throw new BusinessException("Reprocessamento manual e permitido apenas para ERRO_DLQ");
        }

        transferencia.setStatus(StatusTransferencia.PENDENTE);
        transferencia.setUltimoErro(null);
        eventPublisher.publishEvent(new TransferenciaSolicitadaEvent(id));
        return TransferenciaResponse.from(transferencia);
    }

    @Transactional
    public void reconciliar(UUID id, int maxTentativas) {
        transferenciaRepository.findByIdWithLock(id).ifPresent(transferencia -> {
            if (transferencia.getStatus() == StatusTransferencia.CONCLUIDA ||
                    transferencia.getStatus() == StatusTransferencia.ERRO_DLQ) {
                return;
            }

            if (transferencia.getTentativas() >= maxTentativas) {
                transferencia.setStatus(StatusTransferencia.ERRO_DLQ);
                if (transferencia.getUltimoErro() == null) {
                    transferencia.setUltimoErro("Marcada como ERRO_DLQ pela reconciliacao");
                }
                return;
            }

            transferencia.setStatus(StatusTransferencia.PENDENTE);
            eventPublisher.publishEvent(new TransferenciaSolicitadaEvent(id));
        });
    }
}
