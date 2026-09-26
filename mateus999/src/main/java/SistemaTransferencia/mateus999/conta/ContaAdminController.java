package SistemaTransferencia.mateus999.conta;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/admin/contas")
@RequiredArgsConstructor
public class ContaAdminController {

    private final ContaService contaService;

    @PatchMapping("/{id}/status")
    public ContaResponse alterarStatus(@PathVariable UUID id, @Valid @RequestBody AlterarStatusContaRequest request) {
        return contaService.alterarStatusAdmin(id, request.status());
    }

    @PostMapping("/{id}/credito")
    public ContaResponse creditar(@PathVariable UUID id, @Valid @RequestBody CreditoRequest request) {
        return contaService.creditarAdmin(id, request.valor());
    }
}
