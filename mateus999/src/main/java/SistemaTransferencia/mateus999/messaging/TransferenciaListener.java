package SistemaTransferencia.mateus999.messaging;

import SistemaTransferencia.mateus999.entity.Conta;
import SistemaTransferencia.mateus999.entity.Transferencia;
import SistemaTransferencia.mateus999.enumerations.StatusTransferencia;
import SistemaTransferencia.mateus999.exception.SaldoInsuficienteException;
import SistemaTransferencia.mateus999.repository.ContaRepository;
import SistemaTransferencia.mateus999.repository.TransferenciaRepository;
import SistemaTransferencia.mateus999.service.AuditoriaService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class TransferenciaListener {

    private static final Logger log = LoggerFactory.getLogger(TransferenciaListener.class);

    private final TransferenciaRepository transferenciaRepository;
    private final ContaRepository contaRepository;
    private final AuditoriaService auditoriaService;

    public TransferenciaListener(
            TransferenciaRepository transferenciaRepository,
            ContaRepository contaRepository,
            AuditoriaService auditoriaService) {
        this.transferenciaRepository = transferenciaRepository;
        this.contaRepository = contaRepository;
        this.auditoriaService = auditoriaService;
    }

    @SqsListener("${app.sqs.transferencias-queue-name}")
    @Transactional
    public void processar(TransferenciaMessage mensagem) {
        Transferencia transferencia = transferenciaRepository.findById(mensagem.transferenciaId())
                .orElseThrow(() -> new IllegalStateException(
                        "Transferencia nao encontrada para a mensagem: " + mensagem.transferenciaId()));

        if (transferencia.getStatus() != StatusTransferencia.PENDENTE) {
            log.info("Transferencia {} ja estava com status {}, ignorando mensagem duplicada",
                    transferencia.getId(), transferencia.getStatus());
            return;
        }

        try {
            debitarECreditar(transferencia);
            transferencia.setStatus(StatusTransferencia.PROCESSADA);
            transferenciaRepository.save(transferencia);
            auditoriaService.registrar(transferencia.getId(), "PROCESSADA",
                    "Transferencia de " + transferencia.getValor() + " concluida com sucesso");
        } catch (SaldoInsuficienteException ex) {
            transferencia.setStatus(StatusTransferencia.FALHOU);
            transferencia.setMotivoFalha(ex.getMessage());
            transferenciaRepository.save(transferencia);
            auditoriaService.registrar(transferencia.getId(), "FALHOU", ex.getMessage());
            log.warn("Transferencia {} falhou por regra de negocio: {}", transferencia.getId(), ex.getMessage());
        }
    }

    private void debitarECreditar(Transferencia transferencia) {
        UUID idOrigem = transferencia.getContaOrigemId();
        UUID idDestino = transferencia.getContaDestinoId();
        UUID primeiroId = menor(idOrigem, idDestino);
        UUID segundoId = primeiroId.equals(idOrigem) ? idDestino : idOrigem;

        Conta primeira = contaRepository.buscarParaAtualizar(primeiroId)
                .orElseThrow(() -> new IllegalStateException("Conta sumiu: " + primeiroId));
        Conta segunda = contaRepository.buscarParaAtualizar(segundoId)
                .orElseThrow(() -> new IllegalStateException("Conta sumiu: " + segundoId));

        Conta origem = primeira.getId().equals(idOrigem) ? primeira : segunda;
        Conta destino = primeira.getId().equals(idOrigem) ? segunda : primeira;

        if (origem.getSaldo().compareTo(transferencia.getValor()) < 0) {
            throw new SaldoInsuficienteException(origem.getId());
        }

        origem.setSaldo(origem.getSaldo().subtract(transferencia.getValor()));
        destino.setSaldo(destino.getSaldo().add(transferencia.getValor()));

        contaRepository.save(origem);
        contaRepository.save(destino);
    }

    private UUID menor(UUID a, UUID b) {
        return a.compareTo(b) <= 0 ? a : b;
    }
}
