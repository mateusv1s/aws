package SistemaTransferencia.mateus999.transferencia;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferenciaDlqService {

    private final TransferenciaRepository transferenciaRepository;

    @Transactional
    public void marcarComoDlq(UUID id) {
        transferenciaRepository.findByIdWithLock(id).ifPresent(transferencia -> {
            if (transferencia.getStatus() != StatusTransferencia.CONCLUIDA) {
                transferencia.setStatus(StatusTransferencia.ERRO_DLQ);
                if (transferencia.getUltimoErro() == null || transferencia.getUltimoErro().isBlank()) {
                    transferencia.setUltimoErro("Mensagem enviada para a DLQ apos falhas repetidas");
                }
            }
        });
    }
}
