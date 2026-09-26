package SistemaTransferencia.mateus999.conta;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/contas")
@RequiredArgsConstructor
public class ContaController {

    private final ContaService contaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContaResponse criar(Authentication authentication) {
        return contaService.criar(authentication.getName());
    }

    @GetMapping
    public List<ContaResponse> listar(Authentication authentication) {
        return contaService.listarMinhas(authentication.getName());
    }

    @GetMapping("/{id}")
    public ContaResponse buscar(@PathVariable UUID id, Authentication authentication) {
        return contaService.buscar(id, authentication);
    }

    @PatchMapping("/{id}/encerrar")
    public ContaResponse encerrar(@PathVariable UUID id, Authentication authentication) {
        return contaService.encerrarMinha(id, authentication.getName());
    }
}
