package SistemaTransferencia.mateus999.conta;

import SistemaTransferencia.mateus999.auth.Usuario;
import SistemaTransferencia.mateus999.auth.UsuarioRepository;
import SistemaTransferencia.mateus999.exception.BusinessException;
import SistemaTransferencia.mateus999.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContaService {

    private final ContaRepository contaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public ContaResponse criar(String email) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario nao encontrado"));

        Conta conta = Conta.builder()
                .numeroConta(gerarNumeroConta())
                .titular(usuario.getNome())
                .saldo(BigDecimal.ZERO)
                .status(StatusConta.ATIVA)
                .usuario(usuario)
                .build();

        return ContaResponse.from(contaRepository.save(conta));
    }

    @Transactional(readOnly = true)
    public List<ContaResponse> listarMinhas(String email) {
        return contaRepository.findAllByUsuarioEmailIgnoreCaseOrderByCreatedAtDesc(email)
                .stream()
                .map(ContaResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ContaResponse buscar(UUID id, Authentication authentication) {
        if (isAdmin(authentication)) {
            return ContaResponse.from(contaRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Conta nao encontrada")));
        }

        return ContaResponse.from(contaRepository.findByIdAndUsuarioEmailIgnoreCase(id, authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Conta nao encontrada")));
    }

    @Transactional
    public ContaResponse encerrarMinha(UUID id, String email) {
        Conta conta = contaRepository.findByIdWithLock(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta nao encontrada"));

        if (!conta.getUsuario().getEmail().equalsIgnoreCase(email)) {
            throw new ResourceNotFoundException("Conta nao encontrada");
        }
        encerrar(conta);
        return ContaResponse.from(conta);
    }

    @Transactional
    public ContaResponse alterarStatusAdmin(UUID id, StatusConta novoStatus) {
        Conta conta = contaRepository.findByIdWithLock(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta nao encontrada"));

        if (conta.getStatus() == StatusConta.ENCERRADA && novoStatus != StatusConta.ENCERRADA) {
            throw new BusinessException("Conta encerrada nao pode ser reativada");
        }
        if (novoStatus == StatusConta.ENCERRADA) {
            encerrar(conta);
        } else {
            conta.setStatus(novoStatus);
        }
        return ContaResponse.from(conta);
    }

    @Transactional
    public ContaResponse creditarAdmin(UUID id, BigDecimal valor) {
        Conta conta = contaRepository.findByIdWithLock(id)
                .orElseThrow(() -> new ResourceNotFoundException("Conta nao encontrada"));

        if (conta.getStatus() != StatusConta.ATIVA) {
            throw new BusinessException("Somente conta ATIVA pode receber credito administrativo");
        }
        conta.setSaldo(conta.getSaldo().add(valor));
        return ContaResponse.from(conta);
    }

    private void encerrar(Conta conta) {
        if (conta.getStatus() == StatusConta.ENCERRADA) {
            return;
        }
        if (conta.getSaldo().compareTo(BigDecimal.ZERO) != 0) {
            throw new BusinessException("A conta precisa estar com saldo zero para ser encerrada");
        }
        conta.setStatus(StatusConta.ENCERRADA);
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private String gerarNumeroConta() {
        for (int i = 0; i < 20; i++) {
            String numero = String.format("%010d", secureRandom.nextLong(10_000_000_000L));
            if (!contaRepository.existsByNumeroConta(numero)) {
                return numero;
            }
        }
        throw new IllegalStateException("Nao foi possivel gerar numero de conta unico");
    }
}
