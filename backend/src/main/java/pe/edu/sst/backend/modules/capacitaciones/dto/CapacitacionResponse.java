package pe.edu.sst.backend.modules.capacitaciones.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CapacitacionResponse {
    private Long id;
    private String tema;
    private String tipo;
    private LocalDateTime fechaProgramada;
    private BigDecimal duracionHoras;
    private List<CapacitadorResponse> capacitadores;
    private List<String> linksEvaluacion;
    private List<String> linksVideo;
    private String creadoPor;
    private String estado;
    private Integer totalTrabajadores;
    private LocalDateTime createdAt;
}
