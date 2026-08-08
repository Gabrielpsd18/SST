package pe.edu.sst.backend.modules.identity.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import pe.edu.sst.backend.modules.identity.dto.JwtResponse;
import pe.edu.sst.backend.modules.identity.dto.LoginRequest;
import pe.edu.sst.backend.modules.identity.entity.repository.UsuarioRepository;
import pe.edu.sst.backend.modules.identity.enums.RoleName;
import pe.edu.sst.backend.modules.trabajadores.repository.TrabajadorRepository;
import pe.edu.sst.backend.security.jwt.JwtService;
import pe.edu.sst.backend.shared.exception.ResourceNotFoundException;
import pe.edu.sst.backend.modules.identity.entity.Usuario;

@Service
@RequiredArgsConstructor
public class AuthService {

        private final AuthenticationManager authenticationManager;
        private final JwtService jwtService;
        private final UsuarioRepository userRepository;
        private final TrabajadorRepository trabajadorRepository;

        public JwtResponse login(LoginRequest request) {

                authenticationManager.authenticate(

                                new UsernamePasswordAuthenticationToken(
                                                request.getEmail(),
                                                request.getPassword())

                );
                Usuario usuario = userRepository.findByEmail(request.getEmail())
                                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
                String token = jwtService.generateToken(request.getEmail());

                // Obtener el nombre desde el trabajador asociado (o email como fallback)
                String nombreCompleto = trabajadorRepository.findByUsuarioId(usuario.getId())
                                .map(t -> t.getNombres() + " " + t.getApellidos())
                                .orElse(usuario.getEmail());

                String role = (usuario.getRol() != null) ? usuario.getRol().getNombre().name() : RoleName.TRABAJADOR.name();

                if (RoleName.TRABAJADOR.name().equalsIgnoreCase(role)) {
                    throw new pe.edu.sst.backend.shared.exception.BadRequestException(
                            "Acceso restringido: El portal web está reservado para Administradores y Supervisores. Los trabajadores deben ingresar desde la aplicación móvil."
                    );
                }

                return JwtResponse.builder()
                                .accessToken(token)
                                .tokenType("Bearer")
                                .expiresIn(3600L)
                                .email(usuario.getEmail())
                                .nombreCompleto(nombreCompleto)
                                .role(role)
                                .build();

        }

}