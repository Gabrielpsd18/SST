package pe.edu.sst.backend.modules.dashboard.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.edu.sst.backend.modules.capacitaciones.entity.Capacitacion;
import pe.edu.sst.backend.modules.capacitaciones.repository.CapacitacionRepository;
import pe.edu.sst.backend.modules.dashboard.dto.DashboardStatsResponse;
import pe.edu.sst.backend.modules.dashboard.dto.RecentActivityDTO;
import pe.edu.sst.backend.modules.dashboard.dto.UpcomingEventDTO;
import pe.edu.sst.backend.modules.dashboard.service.DashboardService;
import pe.edu.sst.backend.modules.identity.entity.Usuario;
import pe.edu.sst.backend.modules.identity.entity.repository.UsuarioRepository;
import pe.edu.sst.backend.modules.identity.enums.RoleName;
import pe.edu.sst.backend.modules.inspecciones.entity.Inspeccion;
import pe.edu.sst.backend.modules.inspecciones.repository.InspeccionRepository;
import pe.edu.sst.backend.modules.trabajadores.entity.Trabajador;
import pe.edu.sst.backend.modules.trabajadores.repository.TrabajadorRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final TrabajadorRepository trabajadorRepository;
    private final CapacitacionRepository capacitacionRepository;
    private final InspeccionRepository inspeccionRepository;
    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public DashboardStatsResponse obtenerEstadisticas() {
        Long sedeId = obtenerSedeIdFiltro();

        long totalTrabajadores;
        long totalCapacitaciones;
        long totalInspecciones;

        Map<String, Long> trabajadoresPorSede;
        Map<String, Long> trabajadoresPorCargo;
        Map<String, Long> capacitacionesPorEstado;
        Map<String, Long> capacitacionesPorTipo;
        Map<String, Long> inspeccionesPorEstado;
        Map<String, Long> inspeccionesPorTipo;

        List<Capacitacion> recentCapacitaciones;
        List<Inspeccion> recentInspecciones;
        List<Capacitacion> upcomingCapacitaciones;
        List<Inspeccion> upcomingInspecciones;

        if (sedeId != null) {
            // Sede-filtered stats
            totalTrabajadores = trabajadorRepository.countBySedeId(sedeId);
            totalCapacitaciones = capacitacionRepository.countCapacitacionesBySedeId(sedeId);
            totalInspecciones = inspeccionRepository.countInspeccionesBySedeId(sedeId);

            trabajadoresPorCargo = convertToMap(trabajadorRepository.countWorkersByCargoForSede(sedeId));
            capacitacionesPorEstado = convertToMap(capacitacionRepository.countCapacitacionesByStateForSede(sedeId));
            capacitacionesPorTipo = convertToMap(capacitacionRepository.countCapacitacionesByTypeForSede(sedeId));
            inspeccionesPorEstado = convertToMap(inspeccionRepository.countInspeccionesByStateForSede(sedeId));
            inspeccionesPorTipo = convertToMap(inspeccionRepository.countInspeccionesByTypeForSede(sedeId));
            trabajadoresPorSede = new HashMap<>();
            // Query single Sede name for label
            Optional<Trabajador> trabajadorSede = trabajadorRepository
                    .findBySedeId(sedeId, PageRequest.of(0, 1))
                    .getContent()
                    .stream()
                    .findFirst();

            if (trabajadorSede.isPresent() && trabajadorSede.get().getSede() != null) {
                trabajadoresPorSede.put(
                        trabajadorSede.get().getSede().getNombre(),
                        totalTrabajadores);
            }

            recentCapacitaciones = capacitacionRepository.findTop5BySedeIdOrderByIdDesc(sedeId, PageRequest.of(0, 5));
            recentInspecciones = inspeccionRepository.findTop5BySedeIdOrderByIdDesc(sedeId, PageRequest.of(0, 5));

            LocalDateTime nowDateTime = LocalDateTime.now();
            LocalDate nowDate = LocalDate.now();
            upcomingCapacitaciones = capacitacionRepository
                    .findTop5BySedeIdAndFechaProgramadaAfterOrderByFechaProgramadaAsc(sedeId, nowDateTime,
                            PageRequest.of(0, 5));
            upcomingInspecciones = inspeccionRepository
                    .findTop5BySedeIdAndFechaInspeccionAfterOrderByFechaInspeccionAscHoraInspeccionAsc(sedeId, nowDate,
                            PageRequest.of(0, 5));
        } else {
            // General stats
            totalTrabajadores = trabajadorRepository.count();
            totalCapacitaciones = capacitacionRepository.count();
            totalInspecciones = inspeccionRepository.count();

            trabajadoresPorSede = convertToMap(trabajadorRepository.countWorkersBySedeAll());
            trabajadoresPorCargo = convertToMap(trabajadorRepository.countWorkersByCargoAll());
            capacitacionesPorEstado = convertToMap(capacitacionRepository.countCapacitacionesByStateAll());
            capacitacionesPorTipo = convertToMap(capacitacionRepository.countCapacitacionesByTypeAll());
            inspeccionesPorEstado = convertToMap(inspeccionRepository.countInspeccionesByStateAll());
            inspeccionesPorTipo = convertToMap(inspeccionRepository.countInspeccionesByTypeAll());

            recentCapacitaciones = capacitacionRepository.findTop5ByOrderByIdDesc();
            recentInspecciones = inspeccionRepository.findTop5ByOrderByIdDesc();

            LocalDateTime nowDateTime = LocalDateTime.now();
            LocalDate nowDate = LocalDate.now();
            upcomingCapacitaciones = capacitacionRepository
                    .findTop5ByFechaProgramadaAfterOrderByFechaProgramadaAsc(nowDateTime);
            upcomingInspecciones = inspeccionRepository
                    .findTop5ByFechaInspeccionAfterOrderByFechaInspeccionAsc(nowDate);
        }

        // Map Recent Activity
        List<RecentActivityDTO> actividadReciente = new ArrayList<>();
        for (Capacitacion c : recentCapacitaciones) {
            actividadReciente.add(RecentActivityDTO.builder()
                    .id(c.getId())
                    .tipo("CAPACITACION")
                    .titulo(c.getTema())
                    .fecha(c.getCreatedAt() != null ? c.getCreatedAt() : c.getFechaProgramada())
                    .detalle("Tipo: " + c.getTipo())
                    .estado(c.getEstado())
                    .build());
        }
        for (Inspeccion i : recentInspecciones) {
            actividadReciente.add(RecentActivityDTO.builder()
                    .id(i.getId())
                    .tipo("INSPECCION")
                    .titulo(i.getTema())
                    .fecha(i.getCreatedAt() != null ? i.getCreatedAt()
                            : LocalDateTime.of(i.getFechaInspeccion(), i.getHoraInspeccion()))
                    .detalle("Tipo: " + i.getTipo())
                    .estado(i.getEstado())
                    .build());
        }
        actividadReciente.sort((a, b) -> b.getFecha().compareTo(a.getFecha()));
        if (actividadReciente.size() > 5) {
            actividadReciente = actividadReciente.subList(0, 5);
        }

        // Map Upcoming Events
        List<UpcomingEventDTO> proximosEventos = new ArrayList<>();
        for (Capacitacion c : upcomingCapacitaciones) {
            proximosEventos.add(UpcomingEventDTO.builder()
                    .id(c.getId())
                    .tipo("CAPACITACION")
                    .titulo(c.getTema())
                    .fecha(c.getFechaProgramada())
                    .detalle("Duración: " + c.getDuracionHoras() + " hrs")
                    .estado(c.getEstado())
                    .build());
        }
        for (Inspeccion i : upcomingInspecciones) {
            proximosEventos.add(UpcomingEventDTO.builder()
                    .id(i.getId())
                    .tipo("INSPECCION")
                    .titulo(i.getTema())
                    .fecha(LocalDateTime.of(i.getFechaInspeccion(), i.getHoraInspeccion()))
                    .detalle("Tipo: " + i.getTipo())
                    .estado(i.getEstado())
                    .build());
        }
        proximosEventos.sort(Comparator.comparing(UpcomingEventDTO::getFecha));
        if (proximosEventos.size() > 5) {
            proximosEventos = proximosEventos.subList(0, 5);
        }

        return DashboardStatsResponse.builder()
                .totalTrabajadores(totalTrabajadores)
                .totalCapacitaciones(totalCapacitaciones)
                .totalInspecciones(totalInspecciones)
                .trabajadoresPorSede(trabajadoresPorSede)
                .trabajadoresPorCargo(trabajadoresPorCargo)
                .capacitacionesPorEstado(capacitacionesPorEstado)
                .capacitacionesPorTipo(capacitacionesPorTipo)
                .inspeccionesPorEstado(inspeccionesPorEstado)
                .inspeccionesPorTipo(inspeccionesPorTipo)
                .actividadReciente(actividadReciente)
                .proximosEventos(proximosEventos)
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
        Optional<Usuario> optUser = usuarioRepository.findByEmail(email);
        if (optUser.isEmpty()) {
            return null;
        }
        Usuario user = optUser.get();
        if (user.getRol().getNombre() == RoleName.ADMINISTRADOR) {
            return null;
        }
        Optional<Trabajador> optTrabajador = trabajadorRepository.findByUsuarioId(user.getId());
        return optTrabajador.map(t -> t.getSede() != null ? t.getSede().getId() : null).orElse(null);
    }

    private Map<String, Long> convertToMap(List<Object[]> results) {
        Map<String, Long> map = new HashMap<>();
        if (results != null) {
            for (Object[] row : results) {
                if (row != null && row.length >= 2 && row[0] != null) {
                    map.put(row[0].toString(), ((Number) row[1]).longValue());
                }
            }
        }
        return map;
    }
}
