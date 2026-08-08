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

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UsuarioRepository usuarioRepository;
    private final TrabajadorRepository trabajadorRepository;

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

        Trabajador guardado = trabajadorRepository.save(trabajador);
        return mapToProfileResponse(usuario, guardado);
    }

    private UserProfileResponse mapToProfileResponse(Usuario usuario, Trabajador t) {
        return UserProfileResponse.builder()
                .id(usuario.getId())
                .dni(t.getNumeroDocumento())
                .nombres(t.getNombres())
                .apellidos(t.getApellidos())
                .correoCorporativo(usuario.getEmail())
                .sede(t.getSede() != null ? t.getSede().getNombre() : "Sin Sede")
                .area(t.getArea() != null ? t.getArea().getNombre() : "Sin Área")
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
                .nombres("Usuario")
                .apellidos("SST")
                .correoCorporativo(usuario.getEmail())
                .sede("N/A")
                .area("N/A")
                .cargo("N/A")
                .correoNotificaciones(usuario.getEmail())
                .telefono("")
                .capacitacionesCompletadas(0)
                .capacitacionesPendientes(0)
                .build();
    }
}
