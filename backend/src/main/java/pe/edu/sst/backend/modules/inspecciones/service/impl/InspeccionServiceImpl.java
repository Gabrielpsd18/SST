package pe.edu.sst.backend.modules.inspecciones.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.sst.backend.modules.inspecciones.dto.request.CrearInspeccionRequest;
import pe.edu.sst.backend.modules.inspecciones.dto.response.InspeccionResponse;
import pe.edu.sst.backend.modules.inspecciones.entity.Inspeccion;
import pe.edu.sst.backend.modules.inspecciones.repository.InspeccionRepository;
import pe.edu.sst.backend.modules.inspecciones.service.InspeccionService;
import pe.edu.sst.backend.modules.trabajadores.entity.Trabajador;
import pe.edu.sst.backend.modules.trabajadores.repository.TrabajadorRepository;
import pe.edu.sst.backend.shared.exception.BadRequestException;
import pe.edu.sst.backend.shared.exception.ResourceNotFoundException;
import pe.edu.sst.backend.modules.identity.entity.Usuario;
import pe.edu.sst.backend.modules.identity.entity.repository.UsuarioRepository;
import pe.edu.sst.backend.modules.identity.enums.RoleName;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InspeccionServiceImpl implements InspeccionService {

    private final InspeccionRepository inspeccionRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public InspeccionResponse crear(CrearInspeccionRequest request, String userEmail) {
        Set<Trabajador> responsables = resolverResponsables(request.getResponsableIds());

        Inspeccion inspeccion = Inspeccion.builder()
                .tema(request.getTema().trim())
                .tipo(request.getTipo().trim())
                .fechaInspeccion(request.getFechaInspeccion())
                .horaInspeccion(request.getHoraInspeccion())
                .observaciones(request.getObservaciones() != null ? request.getObservaciones().trim() : null)
                .estado("PENDIENTE")
                .creadoPor(userEmail)
                .responsables(responsables)
                .build();

        return mapToResponse(inspeccionRepository.save(inspeccion));
    }

    @Override
    @Transactional
    public InspeccionResponse actualizar(Long id, CrearInspeccionRequest request) {
        Inspeccion inspeccion = inspeccionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inspección no encontrada con ID: " + id));

        Set<Trabajador> responsables = resolverResponsables(request.getResponsableIds());

        inspeccion.setTema(request.getTema().trim());
        inspeccion.setTipo(request.getTipo().trim());
        inspeccion.setFechaInspeccion(request.getFechaInspeccion());
        inspeccion.setHoraInspeccion(request.getHoraInspeccion());
        inspeccion.setObservaciones(request.getObservaciones() != null ? request.getObservaciones().trim() : null);
        inspeccion.setResponsables(responsables);

        return mapToResponse(inspeccionRepository.save(inspeccion));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InspeccionResponse> listarPaginado(Pageable pageable) {
        Long sedeId = obtenerSedeIdFiltro();
        if (sedeId != null) {
            return inspeccionRepository.findBySedeIdOrderByFechaInspeccionDescHoraInspeccionDesc(sedeId, pageable)
                    .map(this::mapToResponse);
        }
        return inspeccionRepository.findAllByOrderByFechaInspeccionDescHoraInspeccionDesc(pageable)
                .map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public InspeccionResponse obtenerPorId(Long id) {
        return inspeccionRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Inspección no encontrada con ID: " + id));
    }

    @Override
    @Transactional
    public void cambiarEstado(Long id, String estado) {
        Inspeccion inspeccion = inspeccionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inspección no encontrada con ID: " + id));

        String estadoNormalizado = estado == null ? "" : estado.trim().toUpperCase();
        if (!List.of("PENDIENTE", "REALIZADA", "RETRASADA", "INCUMPLIDA").contains(estadoNormalizado)) {
            throw new BadRequestException("Estado no válido. Use: PENDIENTE, REALIZADA, RETRASADA o INCUMPLIDA");
        }

        inspeccion.setEstado(estadoNormalizado);
        inspeccionRepository.save(inspeccion);
    }

    private Set<Trabajador> resolverResponsables(List<Long> responsableIds) {
        if (responsableIds == null || responsableIds.isEmpty()) {
            throw new BadRequestException("Debe seleccionar al menos un responsable para la inspección");
        }

        Set<Long> idsUnicos = responsableIds.stream().filter(id -> id != null).collect(Collectors.toSet());
        if (idsUnicos.isEmpty()) {
            throw new BadRequestException("Debe seleccionar al menos un responsable para la inspección");
        }

        List<Trabajador> responsables = trabajadorRepository.findAllById(idsUnicos);
        if (responsables.size() != idsUnicos.size()) {
            throw new ResourceNotFoundException("Uno o más trabajadores seleccionados no existen");
        }

        // Ensure all responsables are ACTIVO
        boolean allActive = responsables.stream().allMatch(t -> t.getEstado() != null && t.getEstado().equalsIgnoreCase("ACTIVO"));
        if (!allActive) {
            throw new BadRequestException("Todos los responsables deben estar en estado ACTIVO");
        }

        return responsables.stream().collect(Collectors.toSet());
    }

    private InspeccionResponse mapToResponse(Inspeccion inspeccion) {
        List<InspeccionResponse.ResponsableInspeccionResponse> responsables = inspeccion.getResponsables() == null
                ? List.of()
                : inspeccion.getResponsables().stream()
                    .sorted(Comparator.comparing(Trabajador::getNombreCompleto, Comparator.nullsLast(String::compareToIgnoreCase)))
                    .map(trabajador -> InspeccionResponse.ResponsableInspeccionResponse.builder()
                            .id(trabajador.getId())
                            .nombreCompleto(trabajador.getNombreCompleto())
                            .cargoNombre(trabajador.getCargo() != null ? trabajador.getCargo().getNombre() : null)
                            .sedeNombre(trabajador.getSede() != null ? trabajador.getSede().getNombre() : null)
                            .build())
                    .toList();

        return InspeccionResponse.builder()
                .id(inspeccion.getId())
                .tema(inspeccion.getTema())
                .tipo(inspeccion.getTipo())
                .fechaInspeccion(inspeccion.getFechaInspeccion())
                .horaInspeccion(inspeccion.getHoraInspeccion())
                .estado(inspeccion.getEstado())
                .observaciones(inspeccion.getObservaciones())
                .creadoPor(inspeccion.getCreadoPor())
                .createdAt(inspeccion.getCreatedAt())
                .responsables(responsables)
                .build();
    }

    private Long obtenerSedeIdFiltro() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        String email = auth.getName();
        if ("admin@sst.com".equals(email)) {
            return null;
        }
        java.util.Optional<Usuario> optUser = usuarioRepository.findByEmail(email);
        if (optUser.isEmpty()) {
            return null;
        }
        Usuario user = optUser.get();
        if (user.getRol().getNombre() == RoleName.ADMINISTRADOR) {
            return null;
        }
        java.util.Optional<Trabajador> optTrabajador = trabajadorRepository.findByUsuarioId(user.getId());
        return optTrabajador.map(t -> t.getSede() != null ? t.getSede().getId() : null).orElse(null);
    }
}
