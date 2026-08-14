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
    @Transactional(readOnly = true)
    public Page<CapacitacionResponse> listarPaginado(Pageable pageable) {
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
                .build();
    }
}
