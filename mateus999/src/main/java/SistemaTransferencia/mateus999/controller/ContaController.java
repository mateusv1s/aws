package SistemaTransferencia.mateus999.controller;

import SistemaTransferencia.mateus999.entity.Conta;
import SistemaTransferencia.mateus999.repository.ContaRepository;
import SistemaTransferencia.mateus999.service.ContaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/spring")
public class ContaController {

    private final ContaService contaService;
    private final ContaRepository contaRepository;

    public ContaController(ContaService contaService, ContaRepository contaRepository) {
        this.contaService = contaService;
        this.contaRepository = contaRepository;
    }

    @PostMapping
    public ResponseEntity<Conta> criarConta() {
        Conta c = new Conta();
        return ResponseEntity.status(HttpStatus.CREATED).body(c);
    }

    @DeleteMapping
    public ResponseEntity<Void> deletarConta(@RequestBody Conta conta) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/att")
    public ResponseEntity<Conta>  atualizarConta (@Valid @RequestBody Conta conta) {
        return ResponseEntity.status(HttpStatus.OK).body(contaRepository.save(conta));
    }

}
