package pe.edu.sst.backend.modules.capacitaciones.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.sst.backend.modules.capacitaciones.dto.CrearCapacitacionRequest;
import pe.edu.sst.backend.modules.capacitaciones.dto.CapacitacionResponse;
import pe.edu.sst.backend.modules.capacitaciones.dto.CapacitadorResponse;
import pe.edu.sst.backend.modules.capacitaciones.entity.*;
import pe.edu.sst.backend.modules.capacitaciones.repository.CapacitacionRepository;
import pe.edu.sst.backend.modules.capacitaciones.repository.CapacitadorRepository;
import pe.edu.sst.backend.modules.capacitaciones.service.CapacitacionService;
import pe.edu.sst.backend.modules.trabajadores.entity.Trabajador;
import pe.edu.sst.backend.modules.trabajadores.repository.TrabajadorRepository;
import pe.edu.sst.backend.modules.identity.entity.Usuario;
import pe.edu.sst.backend.modules.identity.entity.repository.UsuarioRepository;
import pe.edu.sst.backend.modules.identity.enums.RoleName;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import pe.edu.sst.backend.shared.exception.ResourceNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CapacitacionServiceImpl implements CapacitacionService {

    private final CapacitacionRepository capacitacionRepository;
    private final CapacitadorRepository capacitadorRepository;
    private final TrabajadorRepository trabajadorRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public CapacitacionResponse programar(CrearCapacitacionRequest request, String userEmail) {
        // Cargar los capacitadores solicitados (al menos uno)
        java.util.List<Long> idsToLoad = request.getCapacitadorIds() != null && !request.getCapacitadorIds().isEmpty()
                ? request.getCapacitadorIds()
                : (request.getCapacitadorId() != null ? java.util.List.of(request.getCapacitadorId()) : java.util.List.of());

        java.util.List<Capacitador> capacitadores = capacitadorRepository.findAllById(idsToLoad);
        if (capacitadores.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron capacitadores con los IDs proporcionados");
        }

        Capacitacion capacitacion = Capacitacion.builder()
                .tema(request.getTema())
                .tipo(request.getTipo())
                .fechaProgramada(request.getFechaProgramada())
                .duracionHoras(request.getDuracionHoras())
                .capacitadores(new HashSet<>(capacitadores))
                .linksEvaluacion(request.getLinksEvaluacion() != null ? new java.util.HashSet<>(request.getLinksEvaluacion()) : new HashSet<>())
                .linksVideo(request.getLinksVideo() != null ? new java.util.HashSet<>(request.getLinksVideo()) : new HashSet<>())
                .creadoPor(userEmail)
                .estado("PROGRAMADO")
                .build();

        // 1. Resolver trabajadores a asignar de forma masiva o individual
        Set<Trabajador> trabajadoresAAsignar = new HashSet<>();

        if (request.getTrabajadoresIds() != null && !request.getTrabajadoresIds().isEmpty()) {
            trabajadoresAAsignar.addAll(trabajadorRepository.findAllById(request.getTrabajadoresIds()));
        }

        if (request.getSedeIdFilter() != null) {
            List<Trabajador> todos = trabajadorRepository.findAll();
            for (Trabajador t : todos) {
                boolean matchSede = t.getSede() != null && t.getSede().getId().equals(request.getSedeIdFilter());
                if (matchSede && "ACTIVO".equalsIgnoreCase(t.getEstado())) {
                    trabajadoresAAsignar.add(t);
                }
            }
        }

        // Si no se envió filtro específico, por defecto se asigna a todos los trabajadores activos
        if (trabajadoresAAsignar.isEmpty()) {
            trabajadoresAAsignar.addAll(trabajadorRepository.findAll().stream().filter(t -> "ACTIVO".equalsIgnoreCase(t.getEstado())).toList());
        }

        Capacitacion guardada = capacitacionRepository.save(capacitacion);

        Set<CapacitacionTrabajador> asignaciones = new HashSet<>();
        for (Trabajador t : trabajadoresAAsignar) {
            CapacitacionTrabajador ct = CapacitacionTrabajador.builder()
                    .id(new CapacitacionTrabajadorId(guardada.getId(), t.getId()))
                    .capacitacion(guardada)
                    .trabajador(t)
                    .asistencia("PENDIENTE")
                    .build();
            asignaciones.add(ct);
        }
        guardada.getTrabajadoresAsignados().addAll(asignaciones);

        return mapToResponse(capacitacionRepository.save(guardada));
    }

    @Override
    @Transactional
    public CapacitacionResponse actualizar(Long id, CrearCapacitacionRequest request) {
        Capacitacion existing = capacitacionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Capacitación no encontrada con ID: " + id));

        // Update basic fields
        existing.setTema(request.getTema());
        existing.setTipo(request.getTipo());
        existing.setFechaProgramada(request.getFechaProgramada());
        existing.setDuracionHoras(request.getDuracionHoras());

        // Update capacitadores if provided
        java.util.List<Long> idsToLoad = request.getCapacitadorIds() != null && !request.getCapacitadorIds().isEmpty()
                ? request.getCapacitadorIds()
                : (request.getCapacitadorId() != null ? java.util.List.of(request.getCapacitadorId()) : java.util.List.of());
        if (!idsToLoad.isEmpty()) {
            java.util.List<Capacitador> capacitadores = capacitadorRepository.findAllById(idsToLoad);
            if (capacitadores.isEmpty()) {
                throw new ResourceNotFoundException("No se encontraron capacitadores con los IDs proporcionados");
            }
            existing.getCapacitadores().clear();
            existing.getCapacitadores().addAll(new HashSet<>(capacitadores));
        }

        // Update links (replace if provided)
        if (request.getLinksEvaluacion() != null) {
            existing.getLinksEvaluacion().clear();
            existing.getLinksEvaluacion().addAll(new java.util.HashSet<>(request.getLinksEvaluacion()));
        }
        if (request.getLinksVideo() != null) {
            existing.getLinksVideo().clear();
            existing.getLinksVideo().addAll(new java.util.HashSet<>(request.getLinksVideo()));
        }

        // Update trabajadores assignment if explicit list provided
        if (request.getTrabajadoresIds() != null) {
            // Compute current and desired IDs
            java.util.Set<Long> desiredIds = new java.util.HashSet<>(request.getTrabajadoresIds());

            // Current assignments map: trabajadorId -> CapacitacionTrabajador
            java.util.Map<Long, CapacitacionTrabajador> currentMap = existing.getTrabajadoresAsignados().stream()
                    .filter(ct -> ct.getTrabajador() != null && ct.getTrabajador().getId() != null)
                    .collect(java.util.stream.Collectors.toMap(ct -> ct.getTrabajador().getId(), ct -> ct));

            // Remove assignments that are not desired
            java.util.Iterator<CapacitacionTrabajador> it = existing.getTrabajadoresAsignados().iterator();
            while (it.hasNext()) {
                CapacitacionTrabajador ct = it.next();
                Long tId = ct.getTrabajador() != null ? ct.getTrabajador().getId() : null;
                if (tId == null || !desiredIds.contains(tId)) {
                    it.remove(); // will be orphan-removed
                }
            }

            // Add new assignments for IDs that are not currently present
            java.util.Set<Long> toAdd = desiredIds.stream().filter(tid -> !currentMap.containsKey(tid)).collect(java.util.stream.Collectors.toSet());
            if (!toAdd.isEmpty()) {
                java.util.List<Trabajador> trabajadores = trabajadorRepository.findAllById(toAdd);
                for (Trabajador t : trabajadores) {
                    CapacitacionTrabajador ct = CapacitacionTrabajador.builder()
                            .id(new CapacitacionTrabajadorId(existing.getId(), t.getId()))
                            .capacitacion(existing)
                            .trabajador(t)
                            .asistencia("PENDIENTE")
                            .build();
                    existing.getTrabajadoresAsignados().add(ct);
                }
            }
        }

        return mapToResponse(capacitacionRepository.save(existing));
    }
    @Override
    @Transactional(readOnly = true)
    public Page<CapacitacionResponse> listarPaginado(Pageable pageable) {
        Long sedeId = obtenerSedeIdFiltro();
        if (sedeId != null) {
            return capacitacionRepository.findBySedeIdOrderByFechaProgramadaDesc(sedeId, pageable).map(this::mapToResponse);
        }
        return capacitacionRepository.findAllByOrderByFechaProgramadaDesc(pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public CapacitacionResponse obtenerPorId(Long id) {
        return capacitacionRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Capacitación no encontrada con ID: " + id));
    }

    private CapacitacionResponse mapToResponse(Capacitacion c) {
        java.util.List<CapacitadorResponse> capaResp = c.getCapacitadores() != null
                ? c.getCapacitadores().stream().map(cap -> CapacitadorResponse.builder()
                        .id(cap.getId())
                        .nombres(cap.getNombres())
                        .apellidos(cap.getApellidos())
                        .empresa(cap.getEmpresa())
                        .telefono(cap.getTelefono())
                        .correo(cap.getCorreo())
                        .especialidad(cap.getEspecialidad())
                        .estado(cap.getEstado())
                        .build()).toList()
                : java.util.List.of();

        java.util.List<String> linksEval = c.getLinksEvaluacion() != null ? c.getLinksEvaluacion().stream().toList() : java.util.List.of();
        java.util.List<String> linksVid = c.getLinksVideo() != null ? c.getLinksVideo().stream().toList() : java.util.List.of();

        java.util.List<pe.edu.sst.backend.modules.capacitaciones.dto.TrabajadorResumenResponse> trabajadoresResp =
                c.getTrabajadoresAsignados() != null
                ? c.getTrabajadoresAsignados().stream()
                        .filter(ct -> ct.getTrabajador() != null)
                        .map(ct -> {
                            pe.edu.sst.backend.modules.trabajadores.entity.Trabajador t = ct.getTrabajador();
                            return pe.edu.sst.backend.modules.capacitaciones.dto.TrabajadorResumenResponse.builder()
                                    .id(t.getId())
                                    .nombreCompleto(t.getNombreCompleto())
                                    .numeroDocumento(t.getNumeroDocumento())
                                    .sedeNombre(t.getSede() != null ? t.getSede().getNombre() : null)
                                    .cargoNombre(t.getCargo() != null ? t.getCargo().getNombre() : null)
                                    .build();
                        }).toList()
                : java.util.List.of();

        return CapacitacionResponse.builder()
                .id(c.getId())
                .tema(c.getTema())
                .tipo(c.getTipo())
                .fechaProgramada(c.getFechaProgramada())
                .duracionHoras(c.getDuracionHoras())
                .capacitadores(capaResp)
                .linksEvaluacion(linksEval)
                .linksVideo(linksVid)
                .creadoPor(c.getCreadoPor())
                .estado(c.getEstado())
                .totalTrabajadores(c.getTrabajadoresAsignados() != null ? c.getTrabajadoresAsignados().size() : 0)
                .createdAt(c.getCreatedAt())
                .trabajadores(trabajadoresResp)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<pe.edu.sst.backend.modules.capacitaciones.dto.CapacitacionMobileResponse> listarParaMobile(String filtro, Pageable pageable) {
        String filtroNormalizado = (filtro == null || filtro.isBlank()) ? "TODOS" : filtro.trim().toUpperCase();

        Long trabajadorId = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String email = auth.getName();
            java.util.Optional<Usuario> optUser = usuarioRepository.findByEmail(email);
            if (optUser.isPresent()) {
                Usuario user = optUser.get();
                if (user.getRol() != null && user.getRol().getNombre() == RoleName.TRABAJADOR) {
                    trabajadorId = trabajadorRepository.findByUsuarioId(user.getId())
                            .map(Trabajador::getId)
                            .orElse(null);
                }
            }
        }

        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        Page<Capacitacion> pagina = capacitacionRepository.findForMobileWithFilter(trabajadorId, filtroNormalizado, now, pageable);

        final Long currentTrabajadorId = trabajadorId;
        return pagina.map(c -> {
            String capacitadorNombre = (c.getCapacitadores() != null && !c.getCapacitadores().isEmpty())
                    ? c.getCapacitadores().iterator().next().getNombres() + " " + c.getCapacitadores().iterator().next().getApellidos()
                    : "Por asignar";

            String asistencia = "PENDIENTE";
            if (currentTrabajadorId != null && c.getTrabajadoresAsignados() != null) {
                asistencia = c.getTrabajadoresAsignados().stream()
                        .filter(ct -> ct.getTrabajador() != null && currentTrabajadorId.equals(ct.getTrabajador().getId()))
                        .map(CapacitacionTrabajador::getAsistencia)
                        .findFirst()
                        .orElse("PENDIENTE");
            }

            return pe.edu.sst.backend.modules.capacitaciones.dto.CapacitacionMobileResponse.builder()
                    .id(c.getId())
                    .tema(c.getTema())
                    .tipo(c.getTipo())
                    .fechaProgramada(c.getFechaProgramada())
                    .duracionHoras(c.getDuracionHoras())
                    .estado(c.getEstado())
                    .asistencia(asistencia)
                    .capacitadorPrincipal(capacitadorNombre)
                    .linksVideo(c.getLinksVideo() != null ? new java.util.ArrayList<>(c.getLinksVideo()) : java.util.List.of())
                    .linksEvaluacion(c.getLinksEvaluacion() != null ? new java.util.ArrayList<>(c.getLinksEvaluacion()) : java.util.List.of())
                    .build();
        });
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
