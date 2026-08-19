package pe.edu.sst.backend.modules.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsResponse {
    private Long totalTrabajadores;
    private Long totalCapacitaciones;
    private Long totalInspecciones;

    private Map<String, Long> trabajadoresPorSede;
    private Map<String, Long> trabajadoresPorCargo;
    
    private Map<String, Long> capacitacionesPorEstado;
    private Map<String, Long> capacitacionesPorTipo;
    
    private Map<String, Long> inspeccionesPorEstado;
    private Map<String, Long> inspeccionesPorTipo;

    private List<RecentActivityDTO> actividadReciente;
    private List<UpcomingEventDTO> proximosEventos;
}
