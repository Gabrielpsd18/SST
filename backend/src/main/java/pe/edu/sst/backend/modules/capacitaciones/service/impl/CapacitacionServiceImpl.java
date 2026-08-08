package pe.edu.sst.backend.modules.capacitaciones.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.sst.backend.modules.capacitaciones.dto.CrearCapacitacionRequest;
import pe.edu.sst.backend.modules.capacitaciones.dto.CapacitacionResponse;
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
        Capacitador capacitador = capacitadorRepository.findById(request.getCapacitadorId())
                .orElseThrow(() -> new ResourceNotFoundException("Capacitador no encontrado con ID: " + request.getCapacitadorId()));

        Capacitacion capacitacion = Capacitacion.builder()
                .tema(request.getTema())
                .tipo(request.getTipo())
                .fechaProgramada(request.getFechaProgramada())
                .duracionHoras(request.getDuracionHoras())
                .capacitador(capacitador)
                .creadoPor(userEmail)
                .estado("PROGRAMADO")
                .build();

        // 1. Resolver trabajadores a asignar de forma masiva o individual
        Set<Trabajador> trabajadoresAAsignar = new HashSet<>();

        if (request.getTrabajadoresIds() != null && !request.getTrabajadoresIds().isEmpty()) {
            trabajadoresAAsignar.addAll(trabajadorRepository.findAllById(request.getTrabajadoresIds()));
        }

        if (request.getSedeIdFilter() != null || request.getAreaIdFilter() != null) {
            List<Trabajador> todos = trabajadorRepository.findAll();
            for (Trabajador t : todos) {
                boolean matchSede = request.getSedeIdFilter() == null || (t.getSede() != null && t.getSede().getId().equals(request.getSedeIdFilter()));
                boolean matchArea = request.getAreaIdFilter() == null || (t.getArea() != null && t.getArea().getId().equals(request.getAreaIdFilter()));
                if (matchSede && matchArea && "ACTIVO".equalsIgnoreCase(t.getEstado())) {
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
        String capacitadorNombre = c.getCapacitador() != null
                ? c.getCapacitador().getNombres() + " " + (c.getCapacitador().getApellidos() != null ? c.getCapacitador().getApellidos() : "")
                : "Sin Asignar";

        String capacitadorEmpresa = c.getCapacitador() != null ? c.getCapacitador().getEmpresa() : "";

        return CapacitacionResponse.builder()
                .id(c.getId())
                .tema(c.getTema())
                .tipo(c.getTipo())
                .fechaProgramada(c.getFechaProgramada())
                .duracionHoras(c.getDuracionHoras())
                .capacitadorId(c.getCapacitador() != null ? c.getCapacitador().getId() : null)
                .capacitadorNombre(capacitadorNombre.trim())
                .capacitadorEmpresa(capacitadorEmpresa)
                .creadoPor(c.getCreadoPor())
                .estado(c.getEstado())
                .totalTrabajadores(c.getTrabajadoresAsignados() != null ? c.getTrabajadoresAsignados().size() : 0)
                .createdAt(c.getCreatedAt())
                .build();
    }
}
