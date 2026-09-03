package SistemaTransferencia.mateus999.service;

import SistemaTransferencia.mateus999.dto.request.TransferenciaRequest;
import SistemaTransferencia.mateus999.entity.Conta;
import SistemaTransferencia.mateus999.entity.Transferencia;
import SistemaTransferencia.mateus999.enumerations.StatusTransferencia;
import SistemaTransferencia.mateus999.exception.ContaNaoEncontradaException;
import SistemaTransferencia.mateus999.exception.TransferenciaInvalidaException;
import SistemaTransferencia.mateus999.repository.ContaRepository;
import SistemaTransferencia.mateus999.repository.TransferenciaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class TransferenciaService {

    private final TransferenciaRepository transferenciaRepository;
    private final ContaRepository contaRepository;

    public TransferenciaService (TransferenciaRepository transferenciaRepository, ContaRepository contaRepository) {
        this.transferenciaRepository = transferenciaRepository;
        this.contaRepository = contaRepository;
    }

    public Optional<Transferencia> findByIdempotencyKey (String idempotencyKey) {
        return transferenciaRepository.findByIdempotencyKey(idempotencyKey);
    }

    @Transactional
    public Transferencia validacao (TransferenciaRequest request, String idempotencyKey) {
        Conta origem = contaRepository.findById(request.contaOrigemId())
                .orElseThrow(() -> new ContaNaoEncontradaException(request.contaOrigemId()));
        Conta destino = contaRepository.findById(request.contaDestinoId())
                .orElseThrow(() -> new ContaNaoEncontradaException(request.contaDestinoId()));

        if (origem.getId().equals(destino.getId())) {
            throw new TransferenciaInvalidaException("Contas de origem e destino não podem ser iguais");
        }

        Transferencia transferencia = new Transferencia();
        transferencia.setId(UUID.randomUUID());
        transferencia.setIdempotencyKey(idempotencyKey);
        transferencia.setContaOrigemId(origem.getId());
        transferencia.setContaDestinoId(destino.getId());
        transferencia.setValor(request.valor());
        transferencia.setStatus(StatusTransferencia.PENDENTE);

        transferenciaRepository.save(transferencia);

        return transferencia;
    }

}
