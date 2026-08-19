package pe.edu.sst.backend.modules.identity.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.sst.backend.modules.identity.dto.UpdateProfileRequest;
import pe.edu.sst.backend.modules.identity.dto.UserProfileResponse;
import pe.edu.sst.backend.modules.identity.entity.Usuario;
import pe.edu.sst.backend.modules.identity.entity.repository.UsuarioRepository;
import pe.edu.sst.backend.modules.identity.service.UserService;
import pe.edu.sst.backend.modules.trabajadores.entity.Trabajador;
import pe.edu.sst.backend.modules.trabajadores.repository.TrabajadorRepository;
import pe.edu.sst.backend.shared.exception.ResourceNotFoundException;
import pe.edu.sst.backend.shared.exception.BadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import pe.edu.sst.backend.security.jwt.JwtService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsuarioRepository usuarioRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(String userEmail) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + userEmail));

        return trabajadorRepository.findByUsuarioId(usuario.getId())
                .map(t -> mapToProfileResponse(usuario, t))
                .orElseGet(() -> mapToFallbackProfileResponse(usuario));
    }

    @Override
    @Transactional
    public UserProfileResponse updateProfile(String userEmail, UpdateProfileRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + userEmail));

        Trabajador trabajador = trabajadorRepository.findByUsuarioId(usuario.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró información de trabajador asociada al usuario"));

        if (request.getCorreoNotificaciones() != null && !request.getCorreoNotificaciones().isBlank()) {
            trabajador.setCorreoNotificaciones(request.getCorreoNotificaciones());
        }
        if (request.getTelefono() != null && !request.getTelefono().isBlank()) {
            trabajador.setTelefono(request.getTelefono());
        }

        String newToken = null;
        boolean userChanged = false;

        if (request.getEmail() != null && !request.getEmail().isBlank() && !request.getEmail().equalsIgnoreCase(usuario.getEmail())) {
            String newEmail = request.getEmail().trim().toLowerCase();
            if (usuarioRepository.existsByEmail(newEmail)) {
                throw new BadRequestException("El correo corporativo ya está registrado por otro usuario");
            }
            usuario.setEmail(newEmail);
            userChanged = true;
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
            userChanged = true;
        }

        if (userChanged) {
            Usuario usuarioGuardado = usuarioRepository.save(usuario);
            newToken = jwtService.generateToken(usuarioGuardado.getEmail());
        }

        Trabajador guardado = trabajadorRepository.save(trabajador);
        UserProfileResponse response = mapToProfileResponse(usuario, guardado);
        if (newToken != null) {
            response.setToken(newToken);
        }
        return response;
    }

    private UserProfileResponse mapToProfileResponse(Usuario usuario, Trabajador t) {
        return UserProfileResponse.builder()
                .id(usuario.getId())
                .dni(t.getNumeroDocumento())
                .nombreCompleto(t.getNombreCompleto())
                .correoCorporativo(usuario.getEmail())
                .sede(t.getSede() != null ? t.getSede().getNombre() : "Sin Sede")
                .cargo(t.getCargo() != null ? t.getCargo().getNombre() : "Sin Cargo")
                .correoNotificaciones(t.getCorreoNotificaciones() != null ? t.getCorreoNotificaciones() : usuario.getEmail())
                .telefono(t.getTelefono() != null ? t.getTelefono() : "")
                .capacitacionesCompletadas(0)
                .capacitacionesPendientes(0)
                .build();
    }

    private UserProfileResponse mapToFallbackProfileResponse(Usuario usuario) {
        return UserProfileResponse.builder()
                .id(usuario.getId())
                .dni("SIN DNI")
                .nombreCompleto("Usuario SST")
                .correoCorporativo(usuario.getEmail())
                .sede("N/A")
                .cargo("N/A")
                .correoNotificaciones(usuario.getEmail())
                .telefono("")
                .capacitacionesCompletadas(0)
                .capacitacionesPendientes(0)
                .build();
    }
}
