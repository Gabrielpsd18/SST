package pe.edu.sst.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import pe.edu.sst.backend.modules.identity.entity.Rol;
import pe.edu.sst.backend.modules.identity.entity.Usuario;
import pe.edu.sst.backend.modules.identity.enums.RoleName;
import pe.edu.sst.backend.modules.identity.entity.repository.RolRepository;
import pe.edu.sst.backend.modules.identity.entity.repository.UsuarioRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        if (usuarioRepository.existsByEmail("admin@sst.com")) {
            return;
        }

        Rol rolAdmin = rolRepository.findByNombre(RoleName.ADMINISTRADOR)
                .orElseThrow(() -> new RuntimeException("No existe el rol ADMINISTRADOR"));

        Usuario admin = Usuario.builder()
                .email("admin@sst.com")
                .password(passwordEncoder.encode("admin123"))
                .activo(true)
                .rol(rolAdmin)
                .build();

        usuarioRepository.save(admin);

        System.out.println("========================================");
        System.out.println("Administrador creado");
        System.out.println("Email: admin@sst.com");
        System.out.println("Password: admin123");
        System.out.println("========================================");
    }
}