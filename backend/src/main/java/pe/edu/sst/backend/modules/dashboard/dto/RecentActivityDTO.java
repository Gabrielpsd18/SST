package pe.edu.sst.backend.modules.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecentActivityDTO {
    private Long id;
    private String tipo; // "CAPACITACION", "INSPECCION"
    private String titulo;
    private LocalDateTime fecha;
    private String detalle;
    private String estado;
}
