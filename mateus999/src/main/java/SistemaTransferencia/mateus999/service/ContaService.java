package SistemaTransferencia.mateus999.service;

import SistemaTransferencia.mateus999.entity.Conta;
import SistemaTransferencia.mateus999.repository.ContaRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class ContaService {

    private final ContaRepository contaRepository;

    public ContaService(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    public ResponseEntity<Conta> criarConta (@Valid @RequestBody Conta conta) {
        Conta salvarConta = contaRepository.save(conta);
        return ResponseEntity.status(HttpStatus.CREATED).body(salvarConta);
    }

    public ResponseEntity<Void> deletarConta (@Valid @RequestBody Conta conta) {
        contaRepository.delete(conta);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }




}
