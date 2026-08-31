package pe.edu.sst.backend.modules.capacitaciones.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapacitacionMobileResponse {
    private Long id;
    private String tema;
    private String tipo;
    private LocalDateTime fechaProgramada;
    private BigDecimal duracionHoras;
    private String estado;
    private String asistencia;
    private String capacitadorPrincipal;
    private List<String> linksVideo;
    private List<String> linksEvaluacion;
}
