package SistemaTransferencia.mateus999.service;

import SistemaTransferencia.mateus999.entity.Auditoria;
import SistemaTransferencia.mateus999.repository.AuditoriaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    public void registrar(UUID transferenciaId, String evento, String detalhes) {
        auditoriaRepository.save(Auditoria.de(transferenciaId, evento, detalhes));
    }

    public List<Auditoria> historico(UUID transferenciaId) {
        return auditoriaRepository.findByTransferenciaIdOrderByCriadaEmAsc(transferenciaId);
    }
}
