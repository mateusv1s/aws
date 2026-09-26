package SistemaTransferencia.mateus999.config;

import SistemaTransferencia.mateus999.auth.Role;
import SistemaTransferencia.mateus999.auth.Usuario;
import SistemaTransferencia.mateus999.auth.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class AdminSeeder implements ApplicationRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Override
    public void run(ApplicationArguments args) {
        if (!StringUtils.hasText(adminEmail) || !StringUtils.hasText(adminPassword)) {
            return;
        }

        String email = adminEmail.trim().toLowerCase();
        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            return;
        }

        usuarioRepository.save(Usuario.builder()
                .nome("Administrador")
                .email(email)
                .senha(passwordEncoder.encode(adminPassword))
                .role(Role.ADMIN)
                .enabled(true)
                .build());
    }
}
