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
    private Long capacitadorId;
    private String capacitadorNombre;
    private String capacitadorEmpresa;
    private String creadoPor;
    private String estado;
    private Integer totalTrabajadores;
    private LocalDateTime createdAt;
}
