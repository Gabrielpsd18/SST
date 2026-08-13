package pe.edu.sst.backend.modules.inspecciones.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InspeccionResponse {

    private Long id;
    private String tema;
    private String tipo;
    private LocalDate fechaInspeccion;
    private LocalTime horaInspeccion;
    private String estado;
    private String observaciones;
    private String creadoPor;
    private LocalDateTime createdAt;
    private List<ResponsableInspeccionResponse> responsables;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ResponsableInspeccionResponse {
        private Long id;
        private String nombreCompleto;
        private String cargoNombre;
        private String sedeNombre;
    }
}
