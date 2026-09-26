package SistemaTransferencia.mateus999.transferencia;

import SistemaTransferencia.mateus999.conta.Conta;
import SistemaTransferencia.mateus999.conta.ContaRepository;
import SistemaTransferencia.mateus999.conta.StatusConta;
import SistemaTransferencia.mateus999.exception.BusinessException;
import SistemaTransferencia.mateus999.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferenciaProcessor {

    private final TransferenciaRepository transferenciaRepository;
    private final ContaRepository contaRepository;

    @Transactional
    public void processar(UUID transferenciaId) {
        Transferencia transferencia = transferenciaRepository.findByIdWithLock(transferenciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Transferencia nao encontrada"));

        if (transferencia.getStatus() == StatusTransferencia.CONCLUIDA) {
            return;
        }
        if (transferencia.getStatus() == StatusTransferencia.ERRO_DLQ) {
            return;
        }

        transferencia.setStatus(StatusTransferencia.PROCESSANDO);
        transferencia.setUltimoErro(null);

        UUID origemId = transferencia.getContaOrigem().getId();
        UUID destinoId = transferencia.getContaDestino().getId();
        List<UUID> ids = List.of(origemId, destinoId).stream()
                .sorted(Comparator.comparing(UUID::toString))
                .toList();

        Conta primeira = contaRepository.findByIdWithLock(ids.get(0))
                .orElseThrow(() -> new ResourceNotFoundException("Conta nao encontrada"));
        Conta segunda = contaRepository.findByIdWithLock(ids.get(1))
                .orElseThrow(() -> new ResourceNotFoundException("Conta nao encontrada"));

        Conta origem = primeira.getId().equals(origemId) ? primeira : segunda;
        Conta destino = primeira.getId().equals(destinoId) ? primeira : segunda;

        if (origem.getStatus() != StatusConta.ATIVA || destino.getStatus() != StatusConta.ATIVA) {
            throw new BusinessException("Transferencia bloqueada: conta nao esta ATIVA");
        }
        if (origem.getSaldo().compareTo(transferencia.getValor()) < 0) {
            throw new BusinessException("Saldo insuficiente");
        }

        origem.setSaldo(origem.getSaldo().subtract(transferencia.getValor()));
        destino.setSaldo(destino.getSaldo().add(transferencia.getValor()));

        transferencia.setStatus(StatusTransferencia.CONCLUIDA);
        transferencia.setProcessedAt(Instant.now());
        transferencia.setUltimoErro(null);
    }
}
