package SistemaTransferencia.mateus999.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID transferenciaId;

    @Column(nullable = false)
    private String evento;

    @Lob
    private String detalhes;

    private Instant criadaEm;

    public static Auditoria de(UUID transferenciaId, String evento, String detalhes) {
        return new Auditoria(null, transferenciaId, evento, detalhes, Instant.now());
    }
}
