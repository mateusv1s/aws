package SistemaTransferencia.mateus999.service;

import SistemaTransferencia.mateus999.dto.request.AtualizarContaRequest;
import SistemaTransferencia.mateus999.dto.request.CriarContaRequest;
import SistemaTransferencia.mateus999.entity.Conta;
import SistemaTransferencia.mateus999.exception.ContaNaoEncontradaException;
import SistemaTransferencia.mateus999.repository.ContaRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class ContaService {

    private final ContaRepository contaRepository;

    public ContaService(ContaRepository contaRepository) {
        this.contaRepository = contaRepository;
    }

    public Conta criarConta(CriarContaRequest request) {
        Conta conta = new Conta();
        conta.setNome(request.nome());
        conta.setSaldo(request.saldoInicial() != null ? request.saldoInicial() : BigDecimal.ZERO);
        return contaRepository.save(conta);
    }

    public Conta buscarPorId(UUID id) {
        return contaRepository.findById(id)
                .orElseThrow(() -> new ContaNaoEncontradaException(id));
    }

    public List<Conta> listarTodas() {
        return contaRepository.findAll();
    }

    public Conta atualizarConta(UUID id, AtualizarContaRequest request) {
        Conta conta = buscarPorId(id);
        conta.setNome(request.nome());
        return contaRepository.save(conta);
    }

    public void deletarConta(UUID id) {
        Conta conta = buscarPorId(id);
        contaRepository.delete(conta);
    }
}
