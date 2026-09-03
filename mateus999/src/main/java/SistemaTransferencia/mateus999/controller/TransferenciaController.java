package SistemaTransferencia.mateus999.controller;

import SistemaTransferencia.mateus999.dto.request.TransferenciaRequest;
import SistemaTransferencia.mateus999.entity.Transferencia;
import SistemaTransferencia.mateus999.service.TransferenciaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/transf")
public class TransferenciaController {

    private final TransferenciaService transferenciaService;

    public TransferenciaController(TransferenciaService transferenciaService) {
        this.transferenciaService = transferenciaService;
    }

    @GetMapping("/key")
    public Optional<Transferencia> findByIdempotencyKey (String idempotencyKey) {
        return transferenciaService.findByIdempotencyKey(idempotencyKey);
    }
    
    @PostMapping
    public ResponseEntity<Transferencia> criarTransferencia(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @RequestBody @Valid TransferenciaRequest request) {

        Optional<Transferencia> existente = transferenciaService.findByIdempotencyKey(idempotencyKey);
        if (existente.isPresent()) {
            return ResponseEntity.ok(existente.get());
        }

        Transferencia transferencia = transferenciaService.validacao(request, idempotencyKey);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(transferencia);
    }

}
