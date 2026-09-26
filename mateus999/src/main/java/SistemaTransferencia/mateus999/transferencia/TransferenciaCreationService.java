package SistemaTransferencia.mateus999.transferencia;

import SistemaTransferencia.mateus999.auth.Usuario;
import SistemaTransferencia.mateus999.auth.UsuarioRepository;
import SistemaTransferencia.mateus999.conta.Conta;
import SistemaTransferencia.mateus999.conta.ContaRepository;
import SistemaTransferencia.mateus999.conta.StatusConta;
import SistemaTransferencia.mateus999.exception.BusinessException;
import SistemaTransferencia.mateus999.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TransferenciaCreationService {

    private final TransferenciaRepository transferenciaRepository;
    private final ContaRepository contaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public Transferencia criar(String email, String idempotencyKey, TransferenciaRequest request) {
        Transferencia existente = transferenciaRepository
                .findByUsuarioSolicitanteEmailIgnoreCaseAndIdempotencyKey(email, idempotencyKey)
                .orElse(null);
        if (existente != null) {
            return existente;
        }

        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado"));

        Conta origem = contaRepository.findByIdAndUsuarioEmailIgnoreCase(request.contaOrigemId(), email)
                .orElseThrow(() -> new ResourceNotFoundException("Conta de origem nao encontrada"));
        Conta destino = contaRepository.findById(request.contaDestinoId())
                .orElseThrow(() -> new ResourceNotFoundException("Conta de destino nao encontrada"));

        if (origem.getId().equals(destino.getId())) {
            throw new BusinessException("Conta de origem e destino devem ser diferentes");
        }
        if (origem.getStatus() != StatusConta.ATIVA || destino.getStatus() != StatusConta.ATIVA) {
            throw new BusinessException("As duas contas precisam estar ATIVAS");
        }

        Transferencia transferencia = Transferencia.builder()
                .usuarioSolicitante(usuario)
                .contaOrigem(origem)
                .contaDestino(destino)
                .valor(request.valor())
                .status(StatusTransferencia.PENDENTE)
                .idempotencyKey(idempotencyKey)
                .tentativas(0)
                .build();

        Transferencia salva = transferenciaRepository.saveAndFlush(transferencia);
        eventPublisher.publishEvent(new TransferenciaSolicitadaEvent(salva.getId()));
        return salva;
    }
}
