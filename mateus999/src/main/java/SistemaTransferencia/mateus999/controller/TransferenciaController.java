package SistemaTransferencia.mateus999.controller;

import SistemaTransferencia.mateus999.dto.request.TransferenciaRequest;
import SistemaTransferencia.mateus999.dto.response.TransferenciaResponse;
import SistemaTransferencia.mateus999.entity.Transferencia;
import SistemaTransferencia.mateus999.exception.TransferenciaInvalidaException;
import SistemaTransferencia.mateus999.service.TransferenciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/transferencias")
public class TransferenciaController {

    private final TransferenciaService transferenciaService;

    public TransferenciaController(TransferenciaService transferenciaService) {
        this.transferenciaService = transferenciaService;
    }

    @PostMapping
    public ResponseEntity<TransferenciaResponse> criarTransferencia(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody @Valid TransferenciaRequest request) {

        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new TransferenciaInvalidaException("Header Idempotency-Key e obrigatorio");
        }

        Transferencia existente = transferenciaService.findByIdempotencyKey(idempotencyKey).orElse(null);
        if (existente != null) {
            return ResponseEntity.ok(TransferenciaResponse.de(existente));
        }

        Transferencia transferencia = transferenciaService.validarEEnfileirar(request, idempotencyKey);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(TransferenciaResponse.de(transferencia));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransferenciaResponse> buscarPorId(@PathVariable UUID id) {
        return transferenciaService.buscarPorId(id)
                .map(TransferenciaResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/idempotencia/{chave}")
    public ResponseEntity<TransferenciaResponse> buscarPorIdempotencyKey(@PathVariable String chave) {
        return transferenciaService.findByIdempotencyKey(chave)
                .map(TransferenciaResponse::de)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
