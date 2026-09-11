package SistemaTransferencia.mateus999.service;

import SistemaTransferencia.mateus999.dto.request.TransferenciaRequest;
import SistemaTransferencia.mateus999.entity.Conta;
import SistemaTransferencia.mateus999.entity.Transferencia;
import SistemaTransferencia.mateus999.enumerations.StatusTransferencia;
import SistemaTransferencia.mateus999.exception.ContaNaoEncontradaException;
import SistemaTransferencia.mateus999.exception.TransferenciaInvalidaException;
import SistemaTransferencia.mateus999.messaging.TransferenciaProducer;
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
    private final TransferenciaProducer transferenciaProducer;
    private final AuditoriaService auditoriaService;

    public TransferenciaService(
            TransferenciaRepository transferenciaRepository,
            ContaRepository contaRepository,
            TransferenciaProducer transferenciaProducer,
            AuditoriaService auditoriaService) {
        this.transferenciaRepository = transferenciaRepository;
        this.contaRepository = contaRepository;
        this.transferenciaProducer = transferenciaProducer;
        this.auditoriaService = auditoriaService;
    }

    public Optional<Transferencia> buscarPorId(UUID id) {
        return transferenciaRepository.findById(id);
    }

    public Optional<Transferencia> findByIdempotencyKey(String idempotencyKey) {
        return transferenciaRepository.findByIdempotencyKey(idempotencyKey);
    }

    @Transactional
    public Transferencia validarEEnfileirar(TransferenciaRequest request, String idempotencyKey) {
        Conta origem = contaRepository.findById(request.contaOrigemId())
                .orElseThrow(() -> new ContaNaoEncontradaException(request.contaOrigemId()));
        Conta destino = contaRepository.findById(request.contaDestinoId())
                .orElseThrow(() -> new ContaNaoEncontradaException(request.contaDestinoId()));

        if (origem.getId().equals(destino.getId())) {
            throw new TransferenciaInvalidaException("Contas de origem e destino nao podem ser iguais");
        }

        Transferencia transferencia = new Transferencia();
        transferencia.setIdempotencyKey(idempotencyKey);
        transferencia.setContaOrigemId(origem.getId());
        transferencia.setContaDestinoId(destino.getId());
        transferencia.setValor(request.valor());
        transferencia.setStatus(StatusTransferencia.PENDENTE);

        transferencia = transferenciaRepository.save(transferencia);
        auditoriaService.registrar(transferencia.getId(), "ENFILEIRADA",
                "Transferencia de " + request.valor() + " enviada para processamento");

        transferenciaProducer.enviar(transferencia.getId());
        return transferencia;
    }
}
