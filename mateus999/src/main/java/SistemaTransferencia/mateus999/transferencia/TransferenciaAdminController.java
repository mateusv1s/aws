package SistemaTransferencia.mateus999.transferencia;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/transferencias")
@RequiredArgsConstructor
public class TransferenciaAdminController {

    private final TransferenciaRecoveryService recoveryService;

    @PostMapping("/{id}/reprocessar")
    public TransferenciaResponse reprocessar(@PathVariable UUID id) {
        return recoveryService.reprocessarManual(id);
    }
}
