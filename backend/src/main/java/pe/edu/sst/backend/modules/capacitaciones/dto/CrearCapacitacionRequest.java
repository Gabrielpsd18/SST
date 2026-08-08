package pe.edu.sst.backend.modules.capacitaciones.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CrearCapacitacionRequest {

    @NotBlank(message = "El tema de la capacitación es obligatorio")
    private String tema;

    @NotBlank(message = "El tipo de capacitación es obligatorio")
    private String tipo; // CHARLA_5_MINUTOS, INDUCCION, CAPACITACION

    @NotNull(message = "La fecha programada es obligatoria")
    private LocalDateTime fechaProgramada;

    @NotNull(message = "La duración en horas es obligatoria")
    private BigDecimal duracionHoras;

    @NotNull(message = "El capacitador es obligatorio")
    private Long capacitadorId;

    // Filtros de asignación masiva de grupos
    private Long sedeIdFilter;
    private Long areaIdFilter;

    // Lista explícita de IDs de trabajadores a asignar
    private List<Long> trabajadoresIds;
}
