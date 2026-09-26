package SistemaTransferencia.mateus999.transferencia;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/transferencias")
@RequiredArgsConstructor
public class TransferenciaController {

    private final TransferenciaService transferenciaService;

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public TransferenciaResponse solicitar(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody TransferenciaRequest request,
            Authentication authentication
    ) {
        return transferenciaService.solicitar(authentication.getName(), idempotencyKey, request);
    }

    @GetMapping
    public List<TransferenciaResponse> listar(Authentication authentication) {
        return transferenciaService.listar(authentication.getName());
    }

    @GetMapping("/{id}")
    public TransferenciaResponse buscar(@PathVariable UUID id, Authentication authentication) {
        return transferenciaService.buscar(id, authentication);
    }
}
