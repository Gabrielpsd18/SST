package pe.edu.sst.backend.modules.capacitaciones.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private java.util.List<CapacitadorResponse> capacitadores;
    private java.util.List<String> linksEvaluacion;
    private java.util.List<String> linksVideo;
    private String creadoPor;
    private String estado;
    private Integer totalTrabajadores;
    private LocalDateTime createdAt;
}
