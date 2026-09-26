package SistemaTransferencia.mateus999.transferencia;

import SistemaTransferencia.mateus999.exception.BusinessException;
import SistemaTransferencia.mateus999.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransferenciaService {

    private final TransferenciaCreationService creationService;
    private final TransferenciaRepository transferenciaRepository;

    public TransferenciaResponse solicitar(String email, String idempotencyKey, TransferenciaRequest request) {
        if (!StringUtils.hasText(idempotencyKey)) {
            throw new BusinessException("Header Idempotency-Key e obrigatorio");
        }
        String key = idempotencyKey.trim();
        if (key.length() > 120) {
            throw new BusinessException("Idempotency-Key deve ter no maximo 120 caracteres");
        }

        try {
            return TransferenciaResponse.from(creationService.criar(email, key, request));
        } catch (DataIntegrityViolationException ex) {
            return transferenciaRepository
                    .findByUsuarioSolicitanteEmailIgnoreCaseAndIdempotencyKey(email, key)
                    .map(TransferenciaResponse::from)
                    .orElseThrow(() -> ex);
        }
    }

    @Transactional(readOnly = true)
    public List<TransferenciaResponse> listar(String email) {
        return transferenciaRepository.findAllByUsuarioSolicitanteEmailIgnoreCaseOrderByCreatedAtDesc(email)
                .stream()
                .map(TransferenciaResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public TransferenciaResponse buscar(UUID id, Authentication authentication) {
        if (isAdmin(authentication)) {
            return TransferenciaResponse.from(transferenciaRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Transferencia nao encontrada")));
        }

        return TransferenciaResponse.from(
                transferenciaRepository.findByIdAndUsuarioSolicitanteEmailIgnoreCase(id, authentication.getName())
                        .orElseThrow(() -> new ResourceNotFoundException("Transferencia nao encontrada"))
        );
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
