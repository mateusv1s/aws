package SistemaTransferencia.mateus999.controller;

import SistemaTransferencia.mateus999.dto.request.AtualizarContaRequest;
import SistemaTransferencia.mateus999.dto.request.CriarContaRequest;
import SistemaTransferencia.mateus999.dto.response.ContaResponse;
import SistemaTransferencia.mateus999.service.ContaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/contas")
public class ContaController {

    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @PostMapping
    public ResponseEntity<ContaResponse> criarConta(@Valid @RequestBody CriarContaRequest request) {
        ContaResponse resposta = ContaResponse.de(contaService.criarConta(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(resposta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaResponse> buscarConta(@PathVariable UUID id) {
        return ResponseEntity.ok(ContaResponse.de(contaService.buscarPorId(id)));
    }

    @GetMapping
    public ResponseEntity<List<ContaResponse>> listarContas() {
        List<ContaResponse> contas = contaService.listarTodas().stream()
                .map(ContaResponse::de)
                .toList();
        return ResponseEntity.ok(contas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContaResponse> atualizarConta(
            @PathVariable UUID id, @Valid @RequestBody AtualizarContaRequest request) {
        return ResponseEntity.ok(ContaResponse.de(contaService.atualizarConta(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarConta(@PathVariable UUID id) {
        contaService.deletarConta(id);
        return ResponseEntity.noContent().build();
    }
}
